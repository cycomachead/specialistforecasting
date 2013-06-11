/*
 * Copyright, 1999-2006, SALESFORCE.com All Rights Reserved Company Confidential
 */

package tools.xmlloader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.FileTypeMap;

import tools.util.Config;
import tools.apex.ApexLoader;

import com.sforce.soap.partner.sobject.wsc.SObject;
import com.sforce.soap.partner.wsc.PartnerConnection;
import com.sforce.soap.partner.wsc.QueryResult;
import com.sforce.soap.partner.wsc.SaveResult;
import com.sforce.soap.partner.wsc.UpsertResult;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

/**
 * Scontrol/Document loader.
 * 
 * @author stamm
 * @since 146.appstore
 */
public class ScontrolLoader {

    private static final String SCONTROL = "scontrol";
    private static final String BUTTON = "button";
    private static final String WEBLINK = "weblink";
    private static final String SNIPPET = "snippet";
    private static final String DOCUMENT = "document";
    private static final String FOLDER = "folder";
    private static final String APEXPAGE = "apexpage";

    private static final String SCONTROL_SUFFIX = ".scf";
    private static final String APEXPAGE_SUFFIX = ".page";

    public static void main(String args[]) throws Exception {
        try {
            ScontrolLoader l = new ScontrolLoader();
            StringBuilder target = new StringBuilder(128);
            if (args.length > 5) {
                target.append(args[5]); // 
            } else {
                target.append("http://localhost");
            }
            target.append(Config.LOCATION_URL);

            String namespace = null;
            if (args.length > 6) namespace = args[6];
            if (namespace == null || namespace.length() == 0) namespace = null;

            l.loadProject(args[0], args[1], true, args[2], args[3], args[4], target.toString(), namespace, System.out);
        } catch (Exception x) {
            System.err.println("\nScontrolLoader <user> <pw> <scontrol source dir> <manifest dir> <scontrol manifest file>\n");
            throw x;
        }
    }

    private PrintStream output;

    private void out(String str) {
        if (this.output != null) output.println(str);
    }

    private static SimpleDateFormat zulu = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    static {
        zulu.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    static class BaseInfo {
        private String id;
        private final String name;
        private final Date lastModifiedDate;
        private File file;
        // new scontrol name
        private String newName;

        public BaseInfo(String name, File file) {
            this.id = null;
            this.name = name;
            this.lastModifiedDate = null;
            this.file = file;
        }

        public BaseInfo(SObject object) {
            this.id = object.getId().substring(0, 15);
            this.name = (String)object.getField("Name");
            String lmd = (String)object.getField("LastModifiedDate");
            try {
                this.lastModifiedDate = zulu.parse(lmd);
            } catch (Exception e) {
                throw new NumberFormatException(e.toString());
            }
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            if (this.id != null) throw new IllegalStateException("Can only set ID when inserting");
            if (id == null) { throw new IllegalStateException(); }
            this.id = id.substring(0, 15);
        }

        public Date getLastModifiedDate() {
            return lastModifiedDate;
        }

        public String getName() {
            return name;
        }

        public String getNewName() {
            return newName;
        }

        public void setNewName(String name) {
            this.newName = name;
        }

        public File getFile() {
            return this.file;
        }

        public void setFile(File file) {
            if (!file.exists()) { throw new IllegalStateException("File " + file + " doesn't exist"); }
            this.file = file;
        }

        public boolean shouldInsert() {
            return this.id == null;
        }

        public boolean shouldUpdate() {
            // TODO: Go by CRC of the size after replacement, or something like that.
            return this.id != null
                    && (getLastModifiedDate() == null
                            || getLastModifiedDate().before(new Date(getFile().lastModified())) || !name
                            .equals(newName));
        }
    }

    static class FileInfo extends BaseInfo {
        private final long bodyLength;

        public FileInfo(String name, File file) {
            super(name, file);
            this.bodyLength = 0;
        }

        public FileInfo(SObject object) {
            super(object);
            String n = (String)object.getField("BodyLength");
            this.bodyLength = n == null ? 0 : Long.parseLong(n);
        }

        public long getBodyLength() {
            return bodyLength;
        }
    }

    static class ScontrolInfo extends FileInfo {
        private final String developerName;

        public ScontrolInfo(String name, String developerName, File file) {
            super(name, file);
            this.developerName = developerName;
        }

        public ScontrolInfo(SObject object) {
            super(object);
            this.developerName = (String)object.getField("DeveloperName");
        }

        public String getDeveloperName() {
            return developerName;
        }

        @Override
        public boolean shouldUpdate() {
            return super.shouldUpdate() || getBodyLength() == 0;
        }
    }

    static class ApexPageInfo extends BaseInfo {
        private String label;
        private boolean isDummy;

        public ApexPageInfo(String name, String label, File file) {
            super(name, file);
            this.label = label;
        }

        public ApexPageInfo(SObject object) {
            super(object);
            this.label = (String)object.getField("MasterLabel");
            String markup = (String)object.getField("Markup");
            this.isDummy = markup.equals(ApexLoader.DUMMY_MARKUP);
        }

        public String getLabel() {
            return label;
        }

        @Override
        public boolean shouldUpdate() {
            // check if page has dummy markup, force update if it does
            return super.shouldUpdate() || this.isDummy;
        }
    }

    static class ButtonInfo extends BaseInfo {
        private String masterLabel;
        private final String entity;
        private final String displayType;
        private int height;
        private int width;
        private String url;
        private final String linkType;
        private String openType;
        private String scontrolName;
        private String scontrolId;
        private String description;
        private boolean showsLocation;
        private boolean hasScrollbars;
        private boolean hasToolbar;
        private boolean hasMenubar;
        private boolean showsStatus;
        private boolean isResizeable;

        public ButtonInfo(String name, File file) throws IOException {
            super(name, file);
            Properties p = new Properties();
            p.load(new FileInputStream(file));

            entity = p.getProperty("Entity");
            displayType = p.getProperty("DisplayType", "L").substring(0, 1); // The api value is stupid.
            linkType = p.getProperty("LinkType", "url"); // url, sControl, javascript
            setVariables(p);
        }

        public ButtonInfo(SObject object) {
            super(object);
            this.entity = (String)object.getField("PageOrSobjectType");
            this.masterLabel = (String)object.getField("MasterLabel");
            this.displayType = (String)object.getField("DisplayType");
            String heightStr = (String)object.getField("Height");
            this.height = heightStr != null ? Integer.parseInt(heightStr) : 600;
            String widthStr = (String)object.getField("Width");
            this.width = widthStr != null ? Integer.parseInt(widthStr) : 600;
            this.linkType = (String)object.getField("LinkType");
            this.url = (String)object.getField("Url");
            this.openType = (String)object.getField("OpenType");
            this.scontrolId = (String)object.getField("ScontrolId");
            this.showsLocation = "true".equals(object.getField("ShowsLocation"));
            this.hasScrollbars = "true".equals(object.getField("HasScrollbars"));
            this.hasToolbar = "true".equals(object.getField("HasToolbar"));
            this.hasMenubar = "true".equals(object.getField("HasMenubar"));
            this.showsStatus = "true".equals(object.getField("ShowsStatus"));
            this.isResizeable = "true".equals(object.getField("IsResizable"));

        }

        private void setVariables(Properties p) {
            masterLabel = p.getProperty("MasterLabel");
            height = Integer.parseInt(p.getProperty("Height", "600"));
            width = Integer.parseInt(p.getProperty("Width", "400"));
            url = p.getProperty("Url");
            openType = p.getProperty("OpenType", "sidebar");
            scontrolName = p.getProperty("ScontrolName");
            description = p.getProperty("Description");
            showsLocation = "true".equals(p.getProperty("ShowsLocation"));
            hasScrollbars = "true".equals(p.getProperty("HasScrollbars"));
            hasToolbar = "true".equals(p.getProperty("HasToolbar"));
            hasMenubar = "true".equals(p.getProperty("HasMenubar"));
            showsStatus = "true".equals(p.getProperty("ShowsStatus"));
            isResizeable = "true".equals(p.getProperty("IsResizeable"));
        }

        @Override
        public void setFile(File file) {
            super.setFile(file);
            try {
                Properties p = new Properties();
                p.load(new FileInputStream(file));
                setVariables(p);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        public String description() {
            return description;
        }

        public String getDisplayType() {
            return displayType;
        }

        public String getEntity() {
            return entity;
        }

        public int getHeight() {
            return height;
        }

        public String getLinkType() {
            return linkType;
        }

        public String getMasterLabel() {
            return masterLabel;
        }

        public String getOpenType() {
            return openType;
        }

        public String getScontrolId() {
            return scontrolId;
        }

        public String getScontrolName() {
            return scontrolName;
        }

        public String getUrl() {
            return url;
        }

        public int getWidth() {
            return width;
        }

        public String getDescription() {
            return description;
        }

        public boolean isHasMenubar() {
            return hasMenubar;
        }

        public boolean isHasScrollbars() {
            return hasScrollbars;
        }

        public boolean isHasToolbar() {
            return hasToolbar;
        }

        public boolean isResizeable() {
            return isResizeable;
        }

        public boolean isShowsLocation() {
            return showsLocation;
        }

        public boolean isShowsStatus() {
            return showsStatus;
        }

        public void setScontrolId(String scontrolId) {
            this.scontrolId = scontrolId;
        }

        public void setScontrolName(String scontrolName) {
            this.scontrolName = scontrolName;
        }
    }

    static class DocumentInfo extends FileInfo {
        private final String folderId;

        public DocumentInfo(String name, String folderId, File file) {
            super(name, file);
            this.folderId = folderId;
        }

        public DocumentInfo(SObject object) {
            super(object);
            this.folderId = (String)object.getField("FolderId");
        }

        public String getFolderId() {
            return folderId;
        }
    }

    Pattern REPLACE_PATTERN = Pattern.compile("\\{\\#([A-Za-z0-9 ]+)\\#\\}");

    public void loadProject(String username, String password, boolean withExceptions, String projectDirectory, String manifestDirectory,
            String manifestFilename, String loginEndpointUrl, String namespace, PrintStream output)
            throws ConnectionException, IOException {
        this.output = output;
        out("Differential Scontrol Loader");
        long start = System.currentTimeMillis();
        // Login to the Api
        out("Connecting as " + username + " to " + loginEndpointUrl + "\n");
        ConnectorConfig config = new ConnectorConfig();
        config.setUsername(username);
        config.setPassword(password);
        config.setAuthEndpoint(loginEndpointUrl);
        PartnerConnection con = new PartnerConnection(config);

        boolean loadDocs = false;
        boolean loadScontrols = false;
        boolean loadPages = false;
        boolean loadLinks = false;

        // See what kinds of things we need to load
        // TODO: Get the names out here and add the list to the things to query
        File dir = new File(projectDirectory);
		File manifestDir = new File(manifestDirectory);
        File manifestFile = new File(manifestDir, manifestFilename);
        BufferedReader manifestFileRdr = new BufferedReader(new FileReader(manifestFile));
        String nextline;
        while ((nextline = manifestFileRdr.readLine()) != null) {
            if (nextline.startsWith(FOLDER) || nextline.startsWith(DOCUMENT)) {
                loadDocs = true;
            } else if (nextline.startsWith(SCONTROL) || nextline.startsWith(SNIPPET)) {
                loadScontrols = true;
            } else if (nextline.startsWith(APEXPAGE)) {
                loadPages = true;
            } else if (nextline.startsWith(WEBLINK) || nextline.startsWith(BUTTON)) {
                loadLinks = true;
            }
        }

        // get info on all existing scontrols and documents

        // Read the existing metadata info for packages and triggers
        out("Loading existing metadata\n");
        Map<String, ScontrolInfo> scontrols = new HashMap<String, ScontrolInfo>();
        if (loadScontrols) {
            QueryResult r = con.query("select Id,Name,DeveloperName,BodyLength,LastModifiedDate from Scontrol");
            if (r.getSize() > 0) {
                for (SObject s : r.getRecords()) {
                    ScontrolInfo scontrol = new ScontrolInfo(s);
                    scontrols.put(scontrol.getDeveloperName(), scontrol);
                }
            }
        }
        Map<String, DocumentInfo> documents = new HashMap<String, DocumentInfo>();
        if (loadDocs) {
            QueryResult r = con.query("select Id,Name,FolderId,Folder.Name,BodyLength,LastModifiedDate from Document");
            if (r.getSize() > 0) {
                for (SObject s : r.getRecords()) {
                    DocumentInfo document = new DocumentInfo(s);
                    documents.put(document.getName(), document);
                }
            }
            while (!r.isDone()) {
                r = con.queryMore(r.getQueryLocator());
                if (r.getSize() > 0) {
                    for (SObject s : r.getRecords()) {
                        DocumentInfo document = new DocumentInfo(s);
                        documents.put(document.getName(), document);
                    }
                }
            }
        }

        Map<String, ButtonInfo> buttons = new HashMap<String, ButtonInfo>();
        if (loadLinks) {
            QueryResult r = con
                    .query("select Id,Name,MasterLabel,Height,Width,Url,LinkType,OpenType,PageOrSobjectType,LastModifiedDate,"
                            + "HasScrollbars,HasToolbar,HasMenubar,IsResizable,Position,DisplayType,ScontrolId from WebLink");
            if (r.getSize() > 0) {
                for (SObject s : r.getRecords()) {
                    ButtonInfo button = new ButtonInfo(s);
                    buttons.put(button.getName(), button);
                }
            }
        }

        Map<String, String> folders = new HashMap<String, String>();
        if (loadDocs) {
            QueryResult r = con.query("select Id,Name from Folder where Type = 'Document'");
            if (r.getSize() > 0) {
                for (SObject s : r.getRecords()) {
                    folders.put((String)s.getField("Name"), s.getId());
                }
            }
        }

        Map<String, ApexPageInfo> apexpages = new HashMap<String, ApexPageInfo>();
        if (loadPages) {
            QueryResult r = con.query("select Id,Name,Markup,MasterLabel,LastModifiedDate from ApexPage");
            if (r.getSize() > 0) {
                for (SObject s : r.getRecords()) {
                    ApexPageInfo apexpage = new ApexPageInfo(s);
                    apexpages.put(apexpage.getName(), apexpage);
                }
            }
        }

        List<DocumentInfo> documentsToUpsert = new ArrayList<DocumentInfo>();
        List<ScontrolInfo> snippetsToUpsert = new ArrayList<ScontrolInfo>();
        List<ScontrolInfo> scontrolsToUpsert = new ArrayList<ScontrolInfo>();
        List<ButtonInfo> buttonsToUpsert = new ArrayList<ButtonInfo>();
        List<ApexPageInfo> apexPagesToLoad = new ArrayList<ApexPageInfo>();
        int numLoaded = 0;

        out("Loading folders");
        manifestFileRdr = new BufferedReader(new FileReader(manifestFile));
        String currentFolderId = null;
        while ((nextline = manifestFileRdr.readLine()) != null) {
            if (nextline.startsWith(FOLDER)) {
                String folderName = nextline.substring(FOLDER.length() + 1).trim();
                if (folders.containsKey(folderName)) {
                    currentFolderId = folders.get(folderName);
                } else {
                    SObject folder = new SObject();
                    folder.setType("Folder");
                    folder.setField("Name", folderName);
                    folder.setField("Type", "Document");
                    folder.setField("AccessType", "Public");
                    SaveResult[] result = con.create(new SObject[] { folder });
                    if (!result[0].isSuccess()) { throw new IllegalStateException("Couldn't create folder: "
                            + result[0].getErrors()[0].getMessage()); }
                    currentFolderId = result[0].getId();
                    folders.put(folderName, currentFolderId);
                    numLoaded++;
                }
            } else if (nextline.startsWith(DOCUMENT)) {
                if (currentFolderId == null) throw new IllegalStateException("You must specify a folder first");
                // document foo.css Random Document
                StringTokenizer tokenizer = new StringTokenizer(nextline);
                tokenizer.nextToken(); // Remove the document
                String fileName = tokenizer.nextToken();
                String name = tokenizer.nextToken("").trim(); // Allow the name to have spaces
                File file = new File(dir, fileName);

                DocumentInfo info = documents.get(name);
                if (info != null) {
                    info.setFile(file);
                } else {
                    info = new DocumentInfo(name, currentFolderId, new File(dir, fileName));
                    documents.put(name, info);
                }
                documentsToUpsert.add(info);
            } else if (nextline.startsWith(BUTTON) || nextline.startsWith(WEBLINK)) {
                StringTokenizer tokenizer = new StringTokenizer(nextline);
                tokenizer.nextToken(); // Remove the document
                String fileName = tokenizer.nextToken();
                String name = tokenizer.nextToken();
                File file = new File(dir, fileName);

                ButtonInfo info = buttons.get(name);
                if (info != null) {
                    info.setFile(file);
                } else {
                    info = new ButtonInfo(name, file);
                    buttons.put(name, info);
                }
                buttonsToUpsert.add(info);
            } else if (nextline.startsWith(SCONTROL) || nextline.startsWith(SNIPPET)) {
                // scontrol bar.scontrol Snippet_Name Random Document
                StringTokenizer tokenizer = new StringTokenizer(nextline);
                boolean isSnippet = SNIPPET.equals(tokenizer.nextToken()); // Remove the document
                String fileName = tokenizer.nextToken();
                if (fileName.indexOf('.') < 0) fileName = fileName + SCONTROL_SUFFIX;
                String developerName = tokenizer.nextToken();
                String name = tokenizer.nextToken("").trim(); // Allow the name to have spaces
                File file = new File(dir, fileName);

                ScontrolInfo info = scontrols.get(developerName);
                if (info != null) {
                    info.setFile(file);
                    info.setNewName(name);
                } else {
                    info = new ScontrolInfo(name, developerName, new File(dir, fileName));
                    scontrols.put(developerName, info);
                }
                if (isSnippet) {
                    snippetsToUpsert.add(info);
                } else {
                    scontrolsToUpsert.add(info);
                }
            } else if (nextline.startsWith(APEXPAGE)) {
                // apexpage pagefile pagename Page Label
                StringTokenizer tokenizer = new StringTokenizer(nextline);
                tokenizer.nextToken(); // Remove the 'apexpage' prefix
                String fileName = tokenizer.nextToken();
                if (fileName.indexOf('.') < 0) fileName = fileName + APEXPAGE_SUFFIX;
                String name = tokenizer.nextToken(); // name doesn't have spaces
                String label = tokenizer.nextToken("").trim(); // Allow the label to have spaces
                File file = new File(dir, fileName);

                ApexPageInfo info = apexpages.get(name);
                if (info != null) {
                    info.setFile(file);
                    info.setNewName(name);
                } else {
                    info = new ApexPageInfo(name, label, new File(dir, fileName));
                    apexpages.put(name, info);
                }
                apexPagesToLoad.add(info);
            }
        }

        // Deal with all the documents
        List<SObject> sobjectsToUpsert = new ArrayList<SObject>();

        out("Loading documents");

        for (DocumentInfo doc : documentsToUpsert) {
            if (!doc.shouldInsert() && !doc.shouldUpdate()) continue;
            SObject so = new SObject();
            so.setType("Document");
            so.setField("ContentType", FileTypeMap.getDefaultFileTypeMap().getContentType(doc.getFile()));
            so.setField("Body", getByteFileContents(doc.getFile()));
            so.setField("Name", doc.getName());

            if (doc.shouldInsert()) {
                so.setField("FolderId", doc.getFolderId());
                sobjectsToUpsert.add(so);
            } else if (doc.shouldUpdate()) {
                so.setId(doc.getId());
                sobjectsToUpsert.add(so);
            }
        }

        if (sobjectsToUpsert.size() > 0) {
            saveFiles(con, sobjectsToUpsert, "Name", documents);
            out("Loaded " + sobjectsToUpsert.size() + " documents");
            numLoaded += sobjectsToUpsert.size();
        }

        out("Loading webLinks (first pass)");
        sobjectsToUpsert = new ArrayList<SObject>();

        boolean redoAllWeblinks = false;
        for (ButtonInfo but : buttonsToUpsert) {
            if (!but.shouldInsert() && !but.shouldUpdate()) continue;
            if (but.shouldInsert() && but.getScontrolName() != null) continue; // Do this in second pass
            SObject so = getButtonInfo(but, null);
            sobjectsToUpsert.add(so);
        }
        try {
            saveFiles(con, sobjectsToUpsert, "Name", buttons);
        } catch (IllegalStateException ex) {
            // Ignore it,
            redoAllWeblinks = true;
        }
        if (sobjectsToUpsert.size() > 0) {
            out("Loaded " + sobjectsToUpsert.size() + " weblinks/buttons");
            numLoaded += sobjectsToUpsert.size();
        }

        // Insert all the scontrols/snippets with no html in a first pass in case the weblinks refer to tehm.
        List<SObject> sobjectsToUpload = formScontrolList(true, true, snippetsToUpsert, documents, namespace);
        if (sobjectsToUpload.size() > 0) {
            out("Creating snippet stubs");
            saveFiles(con, sobjectsToUpload, "DeveloperName", scontrols);
            out("Created " + snippetsToUpsert.size() + " snippets");
            numLoaded += snippetsToUpsert.size();
        }

        sobjectsToUpload = formScontrolList(false, true, scontrolsToUpsert, documents, namespace);
        if (sobjectsToUpload.size() > 0) {
            out("Creating scontrol stubs");
            saveFiles(con, sobjectsToUpload, "DeveloperName", scontrols);
            out("Created " + snippetsToUpsert.size() + " snippets");
            numLoaded += snippetsToUpsert.size();
        }

        // Then insert the weblinks
        sobjectsToUpsert = new ArrayList<SObject>();
        for (ButtonInfo but : buttonsToUpsert) {
            if (!but.shouldInsert() && !but.shouldUpdate()) continue;
            if (!redoAllWeblinks && (!but.shouldInsert() || but.getScontrolName() == null)) continue;
            SObject so = getButtonInfo(but, null);
            if (but.getScontrolName() != null) {
                ScontrolInfo info = scontrols.get(but.getScontrolName());
                if (info == null) { throw new IllegalStateException("No scontrol with name " + but.getScontrolName()); }
                so.setField("ScontrolId", info.getId());
            }
            sobjectsToUpsert.add(so);
        }
        if (sobjectsToUpsert.size() > 0) {
            saveFiles(con, sobjectsToUpsert, "Name", buttons);
            out("Loaded " + sobjectsToUpsert.size() + " weblinks/buttons in a second pass");
            numLoaded += sobjectsToUpsert.size();
        }

        // Then update all the text.
        out("Loading snippets");
        sobjectsToUpload = formScontrolList(true, false, snippetsToUpsert, documents, namespace);
        if (sobjectsToUpload.size() > 0) {
            saveFiles(con, sobjectsToUpload, "DeveloperName", scontrols);
            out("Loaded " + sobjectsToUpload.size() + " snippets");
            numLoaded += sobjectsToUpload.size();
        }

        out("Loading scontrols");
        sobjectsToUpload = formScontrolList(false, false, scontrolsToUpsert, documents, namespace);
        if (sobjectsToUpload.size() > 0) {
            saveFiles(con, sobjectsToUpload, "DeveloperName", scontrols);
            out("Loaded " + sobjectsToUpload.size() + " scontrols");
            numLoaded += sobjectsToUpload.size();
        }

        // Upsert apex pages
        out("Loading apex pages");

        List<SObject> sobjectsToInsert = new ArrayList<SObject>();
        List<SObject> sobjectsToUpdate = new ArrayList<SObject>();
        for (ApexPageInfo page : apexPagesToLoad) {
            // TODO: AW: Bug #166753.
            // Do this temporarily to force reload of all pages - Insert/Update needs to be used instead of Upsert
            // if (!page.shouldInsert() && !page.shouldUpdate()) continue;
            SObject so = new SObject();
            so.setType("ApexPage");
            so.setField("Markup", getStringFileContents(page.getFile(), namespace).toString());
            so.setField("Name", page.getName());
            so.setField("MasterLabel", page.getLabel());

            if (page.shouldInsert()) {
                sobjectsToInsert.add(so);
            } else {
                so.setField("Id", page.getId());
                sobjectsToUpdate.add(so);
            }
        }

        if (sobjectsToInsert.size() > 0) {
            saveFiles(con, sobjectsToInsert, "Name", apexpages, true);
            out("Created " + sobjectsToInsert.size() + " apex pages");
            numLoaded += sobjectsToInsert.size();
        }

        if (sobjectsToUpdate.size() > 0) {
            saveFiles(con, sobjectsToUpdate, "Name", apexpages, false);
            out("Updated " + sobjectsToUpdate.size() + " apex pages");
            numLoaded += sobjectsToUpdate.size();
        }

        // Now we're done.
        out("\nLoaded " + numLoaded + " files in " + (System.currentTimeMillis() - start) / 1000 + " seconds");
    }

    /**
     * @param con
     * @param sobjectsToUpdate
     * @param string
     * @param apexpages
     * @throws ConnectionException
     */
    private void saveFiles(PartnerConnection con, List<SObject> objects, String keyName,
            Map<String, ApexPageInfo> files, boolean isInsert) throws ConnectionException {
        if (objects == null || objects.size() == 0) return;

        SObject[] array = objects.toArray(new SObject[objects.size()]);

        IllegalStateException ex = null;

        SaveResult[] results = isInsert ? con.create(array) : con.update(array);
        for (int i = 0; i < results.length; i++) {
            SaveResult res = results[i];
            String name = (String)objects.get(i).getField(keyName);
            BaseInfo file = files.get(name);
            if (file == null) { throw new IllegalStateException("Something has gone terribly, terribly wrong"); }

            if (res.isSuccess()) {
                if (isInsert) {
                    file.setId(res.getId()); // 15 character id
                }
            } else {
                ex = new IllegalStateException("Couldn't update " + name + "\n" + res.getErrors()[0].getMessage());
            }
        }
        if (ex != null) { throw ex; }
    }

    private SObject getButtonInfo(ButtonInfo but, Map<String, ScontrolInfo> scontrols) {
        SObject so = new SObject();
        so.setType("WebLink");
        so.setField("Name", but.getName());
        so.setField("Height", but.getHeight());
        so.setField("Width", but.getWidth());
        so.setField("Url", but.getUrl());
        so.setField("OpenType", but.getOpenType());
        so.setField("MasterLabel", but.getMasterLabel());
        so.setField("Description", but.getDescription());
        so.setField("ShowsLocation", but.isShowsLocation());
        so.setField("ShowsStatus", but.isShowsStatus());
        so.setField("HasScrollbars", but.isHasScrollbars());
        so.setField("HasMenubar", but.isHasMenubar());
        so.setField("HasToolbar", but.isHasToolbar());
        so.setField("IsResizable", but.isResizeable());
        if (but.shouldInsert()) {
            so.setField("PageOrSobjectType", but.getEntity());
            so.setField("LinkType", but.getLinkType());
            so.setField("DisplayType", but.getDisplayType());
        } else {
            so.setId(but.getId());
        }
        return so;
    }

    private List<SObject> formScontrolList(boolean isSnippet, boolean forInsert, List<ScontrolInfo> scontrolsToUpsert,
            Map<String, DocumentInfo> documents, String namespace) throws IOException {
        List<SObject> sobjectsToUpsert = new ArrayList<SObject>();
        // Insert the scontrols
        for (ScontrolInfo sc : scontrolsToUpsert) {
            if (!sc.shouldInsert() && !sc.shouldUpdate()) continue;

            // Do the replacement
            StringBuilder rawContents = getStringFileContents(sc.getFile(), namespace);
            String fileContents = replacePattern(REPLACE_PATTERN, rawContents, sc, documents);

            SObject so = new SObject();
            so.setType("Scontrol");
            so.setField("DeveloperName", sc.getDeveloperName());
            so.setField("SupportsCaching", true);
            if (!forInsert) {
                so.setField("HtmlWrapper", fileContents);
            }

            if (sc.shouldInsert()) {
                so.setField("Name", sc.getName());
                so.setField("ContentSource", isSnippet ? "Snippet" : "HTML");
                sobjectsToUpsert.add(so);
            } else if (sc.shouldUpdate() && !forInsert) {
                so.setId(sc.getId());
                so.setField("Name", sc.getNewName());
                sobjectsToUpsert.add(so);
            }
        }
        return sobjectsToUpsert;
    }

    private String replacePattern(Pattern pattern, CharSequence text, FileInfo originalFile,
            Map<String, ? extends FileInfo> replacementMap) {
        StringBuffer result = null;
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            if (result == null) result = new StringBuffer(text.length() + 15);
            String id = matcher.group(1);
            FileInfo info = replacementMap.get(id);
            if (info == null)
                throw new IllegalStateException("Cannot find replacement for " + id + " in " + originalFile.getFile());
            matcher.appendReplacement(result, info.getId());
        }
        if (result == null) return text.toString();
        matcher.appendTail(result);
        return result.toString();
    }

    private void saveFiles(PartnerConnection con, List<SObject> objects, String keyName,
            Map<String, ? extends BaseInfo> files) throws ConnectionException {
        if (objects == null || objects.size() == 0) return;

        SObject[] array = objects.toArray(new SObject[objects.size()]);

        IllegalStateException ex = null;

        UpsertResult[] results = con.upsert("Id", array);
        for (int i = 0; i < results.length; i++) {
            UpsertResult res = results[i];
            String name = (String)objects.get(i).getField(keyName);
            BaseInfo file = files.get(name);
            if (file == null) { throw new IllegalStateException("Something has gone terribly, terribly wrong"); }

            if (res.isSuccess()) {
                if (res.isCreated()) {
                    file.setId(res.getId()); // 15 character id
                }
            } else {
                ex = new IllegalStateException("Couldn't upsert " + name + "\n" + res.getErrors()[0].getMessage());
            }
        }
        if (ex != null) { throw ex; }
    }

    /**
     * @return the contents of the given text file, as a string
     */
    // private static StringBuilder getStringFileContents(File file) throws IOException {
    private static StringBuilder getStringFileContents(File file, String namespace) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = br.readLine()) != null) {
            if (namespace != null) line = insertNamespacePrefix(line, namespace);
            sb.append(line).append('\n');
        }
        return sb;
    }

    private static byte[] getByteFileContents(File file) throws IOException {
        byte[] result = new byte[(int)file.length()];
        ByteBuffer buf = ByteBuffer.wrap(result);
        BufferedInputStream br = new BufferedInputStream(new FileInputStream(file));
        byte[] tb = new byte[1024];
        int size;
        while ((size = br.read(tb)) > 0) {
            buf.put(tb, 0, size);
            if (size < 1024) break;
        }
        return result;
    }

    // Replace {!Quote__c} with {!namespace__Quote__c}
    static Pattern NAMESPACE_PATTERN = Pattern.compile("(\\{\\!)([A-Za-z0-9_]+__c)");

    // Replace {$Action.Quote__c} with {$Action.namespace__Quote__c}
    static Pattern ACTION_PATTERN = Pattern.compile("\\$Action\\.([A-Za-z0-9_]+__c)");

    // Replace {$Scontrol.Quote__c} with {$Scontrol.namespace__Quote__c}
    static Pattern SCONTROL_PATTERN = Pattern.compile("\\{\\!\\$SControl\\.([A-Za-z0-9_]+)");

    // Replace {$ObjectType.Quote__c} with {$ObjectType.namespace__Quote__c}
    static Pattern OBJECTTYPE_PATTERN = Pattern.compile("\\$ObjectType\\.([A-Za-z0-9_]+__c)");

    // Replace {!Object.Contract_Type__c} with {!Object.namespace__Contract_Type__c}
    static Pattern FIELD_PATTERN = Pattern.compile("(\\{\\![A-Za-z0-9_]+\\.)([A-Za-z0-9_]+__c)");

    /**
     * Insert namespace to the custom object definition.
     */
    private static String insertNamespacePrefix(String line, String namespace) {

        String result = line;
        result = insertPrefixForPattern(line, NAMESPACE_PATTERN, namespace + "__");
        result = insertPrefixForPattern(result, ACTION_PATTERN, "\\$Action." + namespace + "__");
        result = insertPrefixForPattern(result, SCONTROL_PATTERN, "\\$SControl." + namespace + "__");
        result = insertPrefixForPattern(result, OBJECTTYPE_PATTERN, "\\$ObjectType." + namespace + "__");
        result = insertPrefixForPattern(result, FIELD_PATTERN, namespace + "__");
        return result;
    }

    /*
     * Insert namespace prefix to a pattern in string.
     */
    private static String insertPrefixForPattern(String line, Pattern pattern, String prefix) {
        StringBuffer result = new StringBuffer();
        Matcher matcher = pattern.matcher(line);

        int gcount = matcher.groupCount();

        if (gcount != 1 && gcount != 2) return line;

        try {
            while (matcher.find()) {
                if (gcount == 1)
                    matcher.appendReplacement(result, prefix + matcher.group(1));
                else if (gcount == 2) matcher.appendReplacement(result, matcher.group(1) + prefix + matcher.group(2));
            }
        } catch (Exception e) {
            System.out.println("Exception in line:" + line);
            System.out.println("Pattern:" + pattern.pattern());
            return line;
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
