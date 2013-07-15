.topTable {
    border: 2px solid #AAA;
    border-collapse: collapse;
    border-spacing: 0;
    table-layout: fixed;
    vertical-align: middle;
    width: 100%;
}

.header {
    background-color: #09F;
    background-image: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#F0FAFF), to(#B4E6FF));
    background-image: -webkit-linear-gradient(top, #F0FAFF, #B4E6FF);
    background-image:    -moz-linear-gradient(top, #F0FAFF, #B4E6FF);
    background-image:     -ms-linear-gradient(top, #F0FAFF, #B4E6FF);
    background-image:      -o-linear-gradient(top, #F0FAFF, #B4E6FF);
    max-width: none;
    padding: 5px;
    width: auto;
}

.headerRow th, .sumRow td {
    border: 2px solid #AAA;
    font-weight: bold;
    text-align: center;
}

.headerRow th {
    font-size: 1.25em;
    padding: 5px;
    white-space: normal;
    word-break: break-word;
}

.sumRow td {
    font-size: 1.15em;
    padding: 4px;
}

tfoot.footer {
    background-color: rgb(109, 135, 168);
}

.cell {
    border: 1px solid #AAA;
    padding: 3px 6px;
    text-align: right;
}

.cell td, .dataRow td, .topTable td,
.dataRow tr > td {
     vertical-align: middle !important;
}

.topTable tr:nth-child(even) .cell {
    background: #FFF;
}

.cell span, .cell td > span {
    margin: 2px 0;
    padding: 3px 6px;
    text-align: right;
    vertical-align: middle;
}

.cell img,
.cell td > span > img,
.cell td > span > a > img {
    margin: 0;
    padding: .2em;
    vertical-align: middle;
}

.no, .no:hover { text-decoration: none; }

.center { text-align: center; }

/* Column used for a scrollbar in the table. */
#scrollbar {
    width: 16px;
}

.leftCol {
    font-weight: bold;
    text-align: left;
    vertical-align: middle;
    padding: .1em .3em;
}

.custPopup {
    background-color: #FFF;
    border: 3px solid #000;
    box-shadow: 5px 5px 15px #888;
    left: 50%;
    padding:10px;
    position: absolute;
    /* These are the 3 css properties you will need to change so the popup
    displays in the center of the screen. First set the width. Then set
    margin-left to negative half of what the width is. You can add
    the height property for a fixed size pop up if you want.*/
    width: 500px;
    margin-left: -250px;
    top:100px;
    z-index: 9999;
}

.custPopupDrilDown{
    background-color: #FFF;
    border: 3px solid #000;
    box-shadow: 5px 5px 15px #888;
    left: 50%;
    padding: 10px;
    position: absolute;
    width: 1100px;
    margin-left: -450px;
    top: 50px;
    z-index: 9999;
}

.popupTable {
    padding: 4px;
    text-align: left;
    width: 100%;
}

.popupTable td {
    float: left;
    padding: 4px;
    text-align: left;
    width: 100px;
}

.blur {
    filter: alpha(opacity = 50);
    filter: blur(2.5px) saturate(50%);
    -o-filter: blur(2.5px) saturate(50%);
    -ms-filter: blur(2.5px) saturate(50%);
    -moz-filter: blur(2.5px) saturate(50%);
    -webkit-filter: blur(2.5px) saturate(50%);
    opacity: .5;
    -o-opacity: .5;
    -ms-opacity: .5;
    -moz-opacity: .5;
    -webkit-opacity: 1;
}
