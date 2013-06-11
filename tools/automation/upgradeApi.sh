#!/usr/bin/env bash
# Warning saying mass update
# option to specify branch
# option to check version number format
# author: rcornel


if [ -z $1  -o -z $2 -o -z $3 ]; then
	echo "Usage: $0 <Api Version> <Change List number> <Absolute path to Branch Location>"
	exit 1;
fi

echo "=========     OBLIGATORY WARNING    ========="
echo "This script performs a mass update to all api versions for all files in a branch. The blood of a thousand developers will be on your hands. Please mail all developers/qe responsible for the files updated before checking this in. So beware of the effects of the mass update. "
echo "========= END OF OBLIGATORY WARNING ========="

read -p "Do you still want to continue [Y/N]? " -n 1
if [[ ! $REPLY =~ ^[Yy]$ ]]
then
    exit 1
fi

apiVersion=$1
changeList=$2
branchLocation=$3

for i in `find $3 | grep -e "cls-meta.xml$" -e "page-meta.xml$"  -e "trigger-meta.xml$" -e "package.xml"`;do
	p4 edit -c $2 $i
	if [ $? -eq 0 ] ; then
	    echo "%s/<apiVersion>\(.*\)<\/apiVersion>/<apiVersion>$apiVersion<\/apiVersion>/
w
q
" | ed $i
	fi;
done
