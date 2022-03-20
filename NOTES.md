
const xmlStr = '<a id="a"><b id="b">hey!</b></a>';
const parser = new DOMParser();
const doc = parser.parseFromString(xmlStr, "application/xml");
// print the name of the root element or error message
const errorNode = doc.querySelector("parsererror");
if (errorNode) {
console.log("error while parsing");
} else {
console.log(doc.documentElement.nodeName);
}

