.sumRow {
    font-weight: bold;
    text-align: center;
    vertical-align: middle;
}

.dataRow {
    text-align: center;
    vertical-align: middle;
}

.apexp .detailList .list td, .apexp .detailList .list th, .apexp .editPage .bPageBlock .detailList .list tr td, .apexp .editPage .bPageBlock .detailList .list tr th {
    padding: 4px 5px;
    white-space: nowrap;
}

.apexp .detailList .list .headerRow th {
    border-bottom: 0 solid #CCCCCC;
    text-align: center;
    vertical-align: middle;
    white-space: pre-wrap;
}

/* Cascade over default table styling */    
body .pbBody table.list tr.dataRow th, body .pbBody table.list tr.dataRow td {
    border-width: 1px;
    vertical-align: middle;
}

body .bRelatedList .pbBody table.list, body .apexp .pbBody table.list {
    border: 1px solid #D4DADC;
    margin-bottom: -0.3em;
}

body .bDetailBlock.bPageBlock .pbBody .labelCol, body .bDetailBlock.bPageBlock .pbBody .dataCol, body .bDetailBlock.bPageBlock .pbBody .data2Col, body .bPageBlock.bEditBlock .pbBody .labelCol, body .bPageBlock.bEditBlock .pbBody .dataCol, body .bPageBlock.bEditBlock .pbBody .data2Col {
    padding-top: 0;
    padding-bottom: 0;
    border-bottom: 0;
}

.topTable {
    border-style: solid;
}

.topTable th {
    background: url("/img/alohaSkin/grid_headerbg.gif") repeat-x scroll 0 bottom #FFFFFF;
    filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#F0FAFF', endColorstr='##B4E6FF'); /* for IE */
    background: -webkit-gradient(linear, left top, left bottom, from(#F0FAFF), to(#B4E6FF)); /* for webkit browsers */
    background: -moz-linear-gradient(top,  #F0FAFF,  #B4E6FF); /* for firefox 3.6+ */
    border-style: solid;
    border-width: 1px;
    padding: .5em;
    text-align: center;
    font-size: 1em;
    vertical-align: middle;
    height:auto;
    cellspacing:2px;       
}

.topTable tr td {
border-style: solid;
border-width: 1px;
height:20px;

}

.leftCol {
font-weight: bold;
text-align: left;
}


.custPopup{
  background-color: white;
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
.popupBackground{
  background-color:black;
  opacity: 0.20;
  filter: alpha(opacity = 20);
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: 9998;
}
.custPopupDrilDown{
  background-color: white;
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
.popupBackgroundDrilDown{
  background-color:black;
  opacity: 0.20;
  filter: alpha(opacity = 20);
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: 9998;
}