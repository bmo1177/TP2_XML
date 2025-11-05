<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    
    <xsl:output method="html" encoding="UTF-8" indent="yes"/>
    
    <!-- Template principal -->
    <xsl:template match="/cvtheque">
        <html>
            <head>
                <meta charset="UTF-8"/>
                <title>CVThèque - Portfolio</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        padding: 20px;
                        min-height: 100vh;
                    }
                    
                    .container {
                        max-width: 1200px;
                        margin: 0 auto;
                    }
                    
                    header {
                        text-align: center;
                        color: white;
                        margin-bottom: 40px;
                    }
                    
                    header h1 {
                        font-size: 3em;
                        margin-bottom: 10px;
                        text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
                    }
                    
                    header p {
                        font-size: 1.2em;
                        opacity: 0.9;
                    }
                    
                    .cv-card {
                        background: white;
                        border-radius: 15px;
                        box-shadow: 0 10px 30px rgba(0,0,0,0.2);
                        margin-bottom: 30px;
                        overflow: hidden;
                        transition: transform 0.3s ease;
                    }
                    
                    .cv-card:hover {
                        transform: translateY(-5px);
                        box-shadow: 0 15px 40px rgba(0,0,0,0.3);
                    }
                    
                    .cv-header {
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        padding: 30px;
                        display: flex;
                        align-items: center;
                        gap: 20px;
                    }
                    
                    .cv-photo {
                        width: 120px;
                        height: 120px;
                        border-radius: 50%;
                        border: 4px solid white;
                        object-fit: cover;
                        background: white;
                    }
                    
                    .cv-header-info h2 {
                        font-size: 2em;
                        margin-bottom: 5px;
                    }
                    
                    .cv-header-info p {
                        opacity: 0.9;
                        margin: 3px 0;
                    }
                    
                    .cv-body {
                        padding: 30px;
                    }
                    
                    .section {
                        margin-bottom: 30px;
                    }
                    
                    .section-title {
                        color: #667eea;
                        font-size: 1.5em;
                        margin-bottom: 15px;
                        padding-bottom: 10px;
                        border-bottom: 3px solid #667eea;
                        display: flex;
                        align-items: center;
                        gap: 10px;
                    }
                    
                    .section-title::before {
                        content: '';
                        width: 8px;
                        height: 8px;
                        background: #667eea;
                        border-radius: 50%;
                    }
                    
                    .item {
                        margin-bottom: 15px;
                        padding: 15px;
                        background: #f8f9fa;
                        border-radius: 8px;
                        border-left: 4px solid #667eea;
                    }
                    
                    .item-title {
                        font-weight: bold;
                        color: #333;
                        font-size: 1.1em;
                        margin-bottom: 5px;
                    }
                    
                    .item-subtitle {
                        color: #666;
                        font-size: 0.9em;
                        margin-bottom: 5px;
                    }
                    
                    .item-details {
                        color: #888;
                        font-size: 0.85em;
                    }
                    
                    .badge {
                        display: inline-block;
                        background: #667eea;
                        color: white;
                        padding: 5px 12px;
                        border-radius: 20px;
                        font-size: 0.85em;
                        margin: 3px;
                    }
                    
                    .badge.expert { background: #28a745; }
                    .badge.avance { background: #17a2b8; }
                    .badge.intermediaire { background: #ffc107; color: #333; }
                    .badge.debutant { background: #6c757d; }
                    
                    .skills-grid {
                        display: grid;
                        grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
                        gap: 10px;
                    }
                    
                    .skill-item {
                        background: #f8f9fa;
                        padding: 10px;
                        border-radius: 8px;
                        border-left: 4px solid #667eea;
                    }
                    
                    .info-grid {
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                        gap: 15px;
                    }
                    
                    .info-box {
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        padding: 20px;
                        border-radius: 10px;
                        text-align: center;
                    }
                    
                    .info-box-value {
                        font-size: 2em;
                        font-weight: bold;
                        margin-bottom: 5px;
                    }
                    
                    .info-box-label {
                        opacity: 0.9;
                        font-size: 0.9em;
                    }
                    
                    footer {
                        text-align: center;
                        color: white;
                        margin-top: 40px;
                        padding: 20px;
                    }
                    
                    .tag-list {
                        display: flex;
                        flex-wrap: wrap;
                        gap: 8px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <header>
                        <h1>📋 CVThèque Professionnelle</h1>
                        <p>Portfolio de <xsl:value-of select="count(cv)"/> candidat(s) qualifié(s)</p>
                    </header>
                    
                    <xsl:apply-templates select="cv"/>
                    
                    <footer>
                        <p>Généré automatiquement depuis XML avec XSLT</p>
                        <p>© 2025 CVThèque - Tous droits réservés</p>
                    </footer>
                </div>
            </body>
        </html>
    </xsl:template>
    
    <!-- Template pour chaque CV -->
    <xsl:template match="cv">
        <div class="cv-card">
            <!-- En-tête du CV -->
            <div class="cv-header">
                <xsl:if test="entete/image">
                    <img class="cv-photo" src="{entete/image/@src}" alt="{entete/image/@alt}"/>
                </xsl:if>
                <xsl:if test="not(entete/image)">
                    <div class="cv-photo" style="display: flex; align-items: center; justify-content: center; font-size: 3em;">👤</div>
                </xsl:if>
                <div class="cv-header-info">
                    <h2><xsl:value-of select="entete/prenom"/><xsl:text> </xsl:text><xsl:value-of select="entete/nom"/></h2>
                    <p>📧 <xsl:value-of select="entete/adresseEmail"/></p>
                    <p>🎂 <xsl:value-of select="entete/dateNaissance"/> • 📍 <xsl:value-of select="entete/lieuNaissance"/></p>
                    <p style="font-size: 0.9em;">ID: <xsl:value-of select="@id"/> | Dernière MAJ: <xsl:value-of select="@derniereMiseAJour"/></p>
                </div>
            </div>
            
            <!-- Corps du CV -->
            <div class="cv-body">
                <!-- Résumé rapide -->
                <div class="info-grid" style="margin-bottom: 30px;">
                    <div class="info-box">
                        <div class="info-box-value"><xsl:value-of select="niveau/@valeur"/></div>
                        <div class="info-box-label">Niveau d'expertise</div>
                    </div>
                    <div class="info-box">
                        <div class="info-box-value"><xsl:value-of select="salaire"/><xsl:text> </xsl:text><xsl:value-of select="salaire/@devise"/></div>
                        <div class="info-box-label">Salaire souhaité (<xsl:value-of select="salaire/@periode"/>)</div>
                    </div>
                    <div class="info-box">
                        <div class="info-box-value"><xsl:value-of select="count(competences/competence)"/></div>
                        <div class="info-box-label">Compétences</div>
                    </div>
                </div>
                
                <!-- Postes occupés -->
                <div class="section">
                    <h3 class="section-title">💼 Expérience Professionnelle</h3>
                    <xsl:for-each select="postes/poste">
                        <div class="item">
                            <div class="item-title"><xsl:value-of select="."/></div>
                            <xsl:if test="@entreprise">
                                <div class="item-subtitle">🏢 <xsl:value-of select="@entreprise"/></div>
                            </xsl:if>
                            <xsl:if test="@dateDebut">
                                <div class="item-details">📅 <xsl:value-of select="@dateDebut"/> - <xsl:value-of select="@dateFin"/></div>
                            </xsl:if>
                        </div>
                    </xsl:for-each>
                </div>
                
                <!-- Diplômes -->
                <div class="section">
                    <h3 class="section-title">🎓 Formation</h3>
                    <xsl:for-each select="diplomes/diplome">
                        <div class="item">
                            <div class="item-title"><xsl:value-of select="intitule"/></div>
                            <div class="item-subtitle">🏫 <xsl:value-of select="etablissement"/></div>
                            <div class="item-details">
                                📅 <xsl:value-of select="anneeObtention"/>
                                <span class="badge"><xsl:value-of select="@niveau"/></span>
                            </div>
                        </div>
                    </xsl:for-each>
                </div>
                
                <!-- Stages (si présents) -->
                <xsl:if test="stages/stage">
                    <div class="section">
                        <h3 class="section-title">🔬 Stages</h3>
                        <xsl:for-each select="stages/stage">
                            <div class="item">
                                <div class="item-title"><xsl:value-of select="titre"/></div>
                                <div class="item-subtitle">🏢 <xsl:value-of select="entrepriseStage"/> • ⏱️ <xsl:value-of select="duree"/></div>
                                <xsl:if test="description">
                                    <p style="margin-top: 8px; color: #666;"><xsl:value-of select="description"/></p>
                                </xsl:if>
                            </div>
                        </xsl:for-each>
                    </div>
                </xsl:if>
                
                <!-- Compétences techniques -->
                <div class="section">
                    <h3 class="section-title">💻 Compétences Techniques</h3>
                    <div class="skills-grid">
                        <xsl:for-each select="competences/competence">
                            <div class="skill-item">
                                <strong><xsl:value-of select="."/></strong>
                                <xsl:if test="@niveauMaitrise">
                                    <br/>
                                    <span class="badge">
                                        <xsl:attribute name="class">
                                            badge <xsl:value-of select="translate(@niveauMaitrise, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>
                                        </xsl:attribute>
                                        <xsl:value-of select="@niveauMaitrise"/>
                                    </span>
                                </xsl:if>
                                <xsl:if test="@categorie">
                                    <br/><small style="color: #888;">📂 <xsl:value-of select="@categorie"/></small>
                                </xsl:if>
                            </div>
                        </xsl:for-each>
                    </div>
                </div>
                
                <!-- Langues -->
                <div class="section">
                    <h3 class="section-title">🌍 Langues</h3>
                    <div class="tag-list">
                        <xsl:for-each select="langues/langue">
                            <div class="badge">
                                <xsl:value-of select="."/> 
                                (Oral: <xsl:value-of select="@niveauOral"/>, 
                                Écrit: <xsl:value-of select="@niveauEcrit"/>)
                            </div>
                        </xsl:for-each>
                    </div>
                </div>
                
                <!-- Loisirs -->
                <div class="section">
                    <h3 class="section-title">🎨 Loisirs &amp; Intérêts</h3>
                    <div class="tag-list">
                        <xsl:for-each select="loisirs/loisir">
                            <span class="badge">
                                <xsl:if test="@type='Sport'">⚽</xsl:if>
                                <xsl:if test="@type='Culture'">📚</xsl:if>
                                <xsl:if test="@type='Technologie'">💻</xsl:if>
                                <xsl:if test="@type='Voyage'">✈️</xsl:if>
                                <xsl:if test="@type='Art'">🎭</xsl:if>
                                <xsl:text> </xsl:text>
                                <xsl:value-of select="."/>
                            </span>
                        </xsl:for-each>
                    </div>
                </div>
            </div>
        </div>
    </xsl:template>
    
</xsl:stylesheet>