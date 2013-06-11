<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
  <html>
  <body>
  <h2>Class Coverage metrics</h2>
	<h3>Based on test execution that completed at : <xsl:value-of select="CoverageResults/timestamp"/></h3>
  <table border="1">
    <tr bgcolor="#9acd32">
	<th align="left">Namespace</th>
	<th align="left">Class Name</th>
	<th align="left">Total Lines</th>
	<th align="left">#Lines Covered</th>
	<th align="left">Perc cov</th>
	<th align="left">Lines not cov</th>
    </tr>
    <xsl:for-each select="CoverageResults/classItem">
    <tr>
      <td><xsl:value-of select="package" /></td>
      <td><xsl:value-of select="classname" /></td>
      <td><xsl:value-of select="totallocations" /></td>
      <td><xsl:value-of select="linescovered" /></td>
      <td><xsl:value-of select="perccovered" /></td>
      <td><xsl:value-of select="linesnotcovered" /></td>
    </tr>
    </xsl:for-each>
  </table>
  </body>
  </html>
</xsl:template>

</xsl:stylesheet>