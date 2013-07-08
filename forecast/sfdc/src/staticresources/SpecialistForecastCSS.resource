.topTable {
    border: 2px solid #AAA;
    width: 100%;
    vertical-align: middle;
    /*max-height: 50px; */
    /*overflow: scroll;*/
    border-collapse:collapse;
    border-spacing: 0;
}

.header {
    background-color: #09F;
    background-image: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#F0FAFF), to(#B4E6FF));
    background-image: -webkit-linear-gradient(top, #F0FAFF, #B4E6FF);
    background-image:    -moz-linear-gradient(top, #F0FAFF, #B4E6FF);
    background-image:     -ms-linear-gradient(top, #F0FAFF, #B4E6FF);
    background-image:      -o-linear-gradient(top, #F0FAFF, #B4E6FF);
    width: auto;
    max-width: none;
    padding: .2em;
/*    position: absolute;
    overflow: none;*/
}

.headerRow th, .sumRow td {
    border: 2px solid #AAA;
    font-weight: bold;
/*    position: absolute;
    overflow: none;*/
    padding: .3em;
    text-align: center;
    font-size: 1.2em;
}

.footer {
    background-color: rgb(109, 135, 168);
    /* matches tab style color, which is 2nd to last column, 3rd down. */
}

tr.dataRow, .dataRow tr > td {
    font-size: .9em;
    text-align: right;
    border: 1px solid #AAA;
    padding: .2em .4em !important;
    vertical-align: middle;
}

/* These two should have fallbacks for IE 7/8 */
tr.dataRow tr:nth-child(even) .cell {
    background: #FFF !important;
}

tr.dataRow tr:nth-child(odd) .cell {
    background: #CCC !important;
}

/* Fixes for amount override buttons not being aligned properly by VF pages */
.dataRow span, .dataRow a {
    vertical-align: middle;
    padding: .1em;
    margin: 0;
}

.dataRow img {
    vertical-align: middle;
    padding: .1em;
    margin: 0;
}

td.userName { /*fix for something better?*/
    text-align: center;
}

/* Column used for a scrollbar in the table. */
#scrollbar {
    width: 16px;
    rowspan: ;
}

.leftCol {
    font-weight: bold;
    text-align: left;
    vertical-align: middle;
    padding: .1em .3em;
}


.custPopup {
    background-color: #FFF;
    border-width: 2px;
    border-style: solid;
    z-index: 9999;
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
}


.popupBackground {
    background-color: rgba(0, 0, 0, .2);
    filter: alpha(opacity = 20); /* ms */
    position: absolute;
    width: 100%;
    height: 100%;
    top: 0;
    left: 0;
    z-index: 9998;
}

.custPopupDrilDown{
    background-color: #FFF;
    border-width: 2px;
    border-style: solid;
    z-index: 9999;
    left: 50%;
    padding:10px;
    position: absolute;
    width: 1100px;
    margin-left: -450px;
    top:50px;
}

.popupBackgroundDrilDown {
    background-color: rgba(0, 0, 0, .2);
    filter: alpha(opacity = 20);
    position: absolute;
    width: 100%;
    height: 100%;
    top: 0;
    left: 0;
    z-index: 9998;
}
