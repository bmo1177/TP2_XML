#!/bin/bash
# Script shell pour automatiser les conversions

echo "==================================="
echo "  Conversion automatique des CV"
echo "==================================="

# Variables
XML_FILE="cv.xml"
XSL_FILE="cv_to_html.xsl"
HTML_FILE="output/cv_export.html"
PDF_FILE="output/cv_export.pdf"
DOC_FILE="output/cv_export.rtf"

# Création du dossier de sortie
mkdir -p output

# Conversion XML vers HTML avec xsltproc
echo -e "\n[1/3] Conversion XML → HTML..."
xsltproc -o $HTML_FILE $XSL_FILE $XML_FILE

if [ $? -eq 0 ]; then
    echo "✓ HTML généré avec succès"
else
    echo "✗ Erreur lors de la génération HTML"
    exit 1
fi

# Conversion HTML vers PDF avec wkhtmltopdf
echo -e "\n[2/3] Conversion HTML → PDF..."
wkhtmltopdf --enable-local-file-access $HTML_FILE $PDF_FILE

if [ $? -eq 0 ]; then
    echo "✓ PDF généré avec succès"
else
    echo "✗ Erreur lors de la génération PDF"
fi

# Conversion avec Java
echo -e "\n[3/3] Conversion XML → DOC..."
java -cp ".:lib/*" CVConverter

echo -e "\n==================================="
echo "  Conversions terminées !"
echo "==================================="
echo "Fichiers générés dans le dossier 'output/'"
ls -lh output/