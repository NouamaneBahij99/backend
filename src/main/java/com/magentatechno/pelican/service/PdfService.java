package com.magentatechno.pelican.service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.magentatechno.pelican.entity.Courrier;
import com.magentatechno.pelican.entity.HistoriqueCourrier;
import com.magentatechno.pelican.exception.ResourceNotFoundException;
import com.magentatechno.pelican.repository.CourrierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final CourrierRepository courrierRepository;

    // Palette Sama Courrier
    private static final DeviceRgb PRIMARY       = new DeviceRgb(91,  33,  182);
    private static final DeviceRgb PRIMARY_DARK  = new DeviceRgb(55,  20,  120);
    private static final DeviceRgb PRIMARY_LIGHT = new DeviceRgb(237, 233, 254);
    private static final DeviceRgb ACCENT        = new DeviceRgb(124, 58,  237);
    private static final DeviceRgb SUCCESS       = new DeviceRgb(16,  185, 129);
    private static final DeviceRgb SUCCESS_LIGHT = new DeviceRgb(209, 250, 229);
    private static final DeviceRgb WARNING       = new DeviceRgb(245, 158, 11);
    private static final DeviceRgb WARNING_LIGHT = new DeviceRgb(254, 243, 199);
    private static final DeviceRgb DANGER        = new DeviceRgb(239, 68,  68);
    private static final DeviceRgb DANGER_LIGHT  = new DeviceRgb(254, 226, 226);
    private static final DeviceRgb INFO          = new DeviceRgb(59,  130, 246);
    private static final DeviceRgb INFO_LIGHT    = new DeviceRgb(219, 234, 254);
    private static final DeviceRgb GRAY_50       = new DeviceRgb(248, 250, 252);
    private static final DeviceRgb GRAY_100      = new DeviceRgb(241, 245, 249);
    private static final DeviceRgb GRAY_200      = new DeviceRgb(226, 232, 240);
    private static final DeviceRgb GRAY_300      = new DeviceRgb(203, 213, 225);
    private static final DeviceRgb GRAY_500      = new DeviceRgb(100, 116, 139);
    private static final DeviceRgb GRAY_700      = new DeviceRgb(51,  65,  85);
    private static final DeviceRgb GRAY_800      = new DeviceRgb(30,  41,  59);
    private static final DeviceRgb GRAY_900      = new DeviceRgb(15,  23,  42);
    private static final DeviceRgb WHITE         = new DeviceRgb(255, 255, 255);
    private static final DeviceRgb PURPLE_SOFT   = new DeviceRgb(196, 181, 253);

    private static final DateTimeFormatter FMT      = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("dd MMMM yyyy", java.util.Locale.FRENCH);

    public byte[] generateCourrierPdf(Long id) {
        Courrier c = courrierRepository.findByIdForPdf(id)
                .orElseThrow(() -> new ResourceNotFoundException("Courrier non trouve"));
        return buildPdf(c);
    }

    private byte[] buildPdf(Courrier c) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter   writer = new PdfWriter(baos);
            PdfDocument pdf    = new PdfDocument(writer);
            Document    doc    = new Document(pdf, PageSize.A4);
            doc.setMargins(0, 0, 0, 0);

            PdfFont regular  = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont bold     = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont italic   = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
            PdfFont boldItal = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLDOBLIQUE);

            // ================================================================
            // HEADER PRINCIPAL
            // ================================================================
            Table header = new Table(UnitValue.createPercentArray(new float[]{55, 45}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setBackgroundColor(PRIMARY);

            // Logo + titre app
            Cell logoCell = new Cell().setBorder(Border.NO_BORDER)
                    .setPaddingLeft(32).setPaddingTop(28).setPaddingBottom(28).setPaddingRight(10);
            logoCell.add(new Paragraph()
                    .add(new Text("SAMA COURRIER\n")
                            .setFont(bold).setFontSize(22).setFontColor(WHITE)
                            .setCharacterSpacing(0f))
                    .add(new Text("by magentatechno")
                            .setFont(italic).setFontSize(10)
                            .setFontColor(PURPLE_SOFT)));
            // Ligne décorative
            logoCell.add(new Paragraph()
                    .add(new Text("Gestion Electronique du Courrier")
                            .setFont(regular).setFontSize(9)
                            .setFontColor(new DeviceRgb(167, 139, 250)))
                    .setMarginTop(4));
            header.addCell(logoCell);

            // Référence document
            Cell refCell = new Cell().setBorder(Border.NO_BORDER)
                    .setPaddingRight(32).setPaddingTop(28).setPaddingBottom(28).setPaddingLeft(10)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);

            refCell.add(new Paragraph()
                    .add(new Text("FICHE COURRIER\n")
                            .setFont(bold).setFontSize(13).setFontColor(PURPLE_SOFT)
                            .setCharacterSpacing(2f)));
            refCell.add(new Paragraph()
                    .add(new Text(c.getNumero())
                            .setFont(boldItal).setFontSize(11).setFontColor(WHITE))
                    .setTextAlignment(TextAlignment.RIGHT));
            refCell.add(new Paragraph()
                    .add(new Text(LocalDateTime.now().format(FMT))
                            .setFont(regular).setFontSize(8)
                            .setFontColor(new DeviceRgb(148, 163, 184)))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginTop(4));
            header.addCell(refCell);
            doc.add(header);

            // ================================================================
            // BANDE TYPE + STATUT + PRIORITE
            // ================================================================
            Table badge = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setBackgroundColor(PRIMARY_DARK);

            String[][] badges = {
                    {"TYPE", c.getType().toString(), "ENTRANT".equals(c.getType().toString()) ? "Entrant" : "Sortant"},
                    {"STATUT", c.getStatut().toString(), getStatutLabel(c.getStatut().toString())},
                    {"PRIORITE", c.getPriorite().toString(), c.getPriorite().toString()},
            };

            for (String[] b : badges) {
                DeviceRgb bColor = "STATUT".equals(b[0])
                        ? getStatutColor(b[1])
                        : "PRIORITE".equals(b[0])
                        ? getPrioriteColor(b[1])
                        : ACCENT;

                Cell bc = new Cell().setBorder(Border.NO_BORDER)
                        .setPaddingTop(10).setPaddingBottom(10)
                        .setTextAlignment(TextAlignment.CENTER);
                bc.add(new Paragraph()
                        .add(new Text(b[0] + "\n")
                                .setFont(regular).setFontSize(7)
                                .setFontColor(new DeviceRgb(148, 163, 184))
                                .setCharacterSpacing(1f))
                        .add(new Text("  " + b[2] + "  ")
                                .setFont(bold).setFontSize(10).setFontColor(WHITE)
                                .setBackgroundColor(bColor, 4f, 2f, 4f, 2f))
                        .setTextAlignment(TextAlignment.CENTER));
                badge.addCell(bc);
            }
            doc.add(badge);

            // Contenu avec marges
            doc.setMargins(0, 36, 36, 36);

            // ================================================================
            // TITRE OBJET
            // ================================================================
            doc.add(new Paragraph()
                    .add(new Text(c.getObjet())
                            .setFont(bold).setFontSize(18).setFontColor(GRAY_900))
                    .setMarginTop(28).setMarginBottom(6)
                    .setPaddingBottom(10)
                    .setBorderBottom(new SolidBorder(PRIMARY_LIGHT, 2f)));

            // ================================================================
            // BLOC EXPEDITEUR / DESTINATAIRE (cartes)
            // ================================================================
            Table parties = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(20).setMarginTop(8);

            // Expéditeur
            Cell expCell = new Cell().setBorder(new SolidBorder(GRAY_200, 1f))
                    .setBorderLeft(new SolidBorder(PRIMARY, 3f))
                    .setPadding(14).setBorderRadius(new BorderRadius(4));
            expCell.add(new Paragraph("DE")
                    .setFont(bold).setFontSize(8).setFontColor(PRIMARY)
                    .setCharacterSpacing(1.5f).setMarginBottom(4));
            expCell.add(new Paragraph(c.getExpediteur())
                    .setFont(bold).setFontSize(12).setFontColor(GRAY_800).setMarginBottom(0));
            parties.addCell(expCell);

            // Destinataire
            Cell destCell = new Cell().setBorder(new SolidBorder(GRAY_200, 1f))
                    .setBorderLeft(new SolidBorder(ACCENT, 3f))
                    .setPadding(14).setBorderRadius(new BorderRadius(4));
            destCell.add(new Paragraph("A")
                    .setFont(bold).setFontSize(8).setFontColor(ACCENT)
                    .setCharacterSpacing(1.5f).setMarginBottom(4));
            destCell.add(new Paragraph(c.getDestinataire())
                    .setFont(bold).setFontSize(12).setFontColor(GRAY_800).setMarginBottom(0));
            parties.addCell(destCell);
            doc.add(parties);

            // ================================================================
            // INFORMATIONS DÉTAILLÉES (grille 2x4)
            // ================================================================
            doc.add(sectionTitle("Informations du courrier", bold, regular));

            Table infoGrid = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(20)
                    .setBorder(new SolidBorder(GRAY_200, 1f))
                    .setBorderRadius(new BorderRadius(6));

            String createdAt = c.getCreatedAt() != null ? c.getCreatedAt().format(FMT) : "-";
            String updatedAt = c.getUpdatedAt() != null ? c.getUpdatedAt().format(FMT) : "-";
            String createurNom = c.getCreateur() != null
                    ? c.getCreateur().getPrenom() + " " + c.getCreateur().getNom() : "-";
            String assigneNom = c.getAssigneA() != null
                    ? c.getAssigneA().getPrenom() + " " + c.getAssigneA().getNom() : "Non assigne";
            String workflowNom = c.getWorkflow() != null ? c.getWorkflow().getNom() : "-";
            String etapeNom = c.getEtapeCourante() != null ? c.getEtapeCourante().getNom() : "-";

            Object[][] infoRows = {
                    {"Reference", c.getNumero(), "Date reception", createdAt},
                    {"Cree par", createurNom, "Assigne a", assigneNom},
                    {"Workflow", workflowNom, "Etape courante", etapeNom},
                    {"Derniere MAJ", updatedAt, "Archive", c.isArchive() ? "Oui" : "Non"},
            };

            boolean rowShade = false;
            for (Object[] row : infoRows) {
                DeviceRgb bg = rowShade ? GRAY_50 : WHITE;
                for (int i = 0; i < 4; i++) {
                    Cell ic = new Cell().setBorder(Border.NO_BORDER)
                            .setBorderBottom(new SolidBorder(GRAY_100, 0.5f))
                            .setBorderRight(i % 2 == 0 ? new SolidBorder(GRAY_200, 0.5f) : Border.NO_BORDER)
                            .setBackgroundColor(bg).setPaddingTop(10).setPaddingBottom(10)
                            .setPaddingLeft(14).setPaddingRight(14);
                    if (i % 2 == 0) {
                        // Label
                        ic.add(new Paragraph(row[i].toString())
                                .setFont(regular).setFontSize(8)
                                .setFontColor(GRAY_500).setMarginBottom(2));
                    } else {
                        // Valeur
                        ic.add(new Paragraph(row[i].toString())
                                .setFont(bold).setFontSize(10).setFontColor(GRAY_800));
                    }
                    infoGrid.addCell(ic);
                }
                rowShade = !rowShade;
            }
            doc.add(infoGrid);

            // ================================================================
            // CONTENU / RESUME
            // ================================================================
            if (c.getContenu() != null && !c.getContenu().isBlank()) {
                doc.add(sectionTitle("Contenu du courrier", bold, regular));
                doc.add(new Paragraph(c.getContenu())
                        .setFont(regular).setFontSize(10).setFontColor(GRAY_700)
                        
                        .setBackgroundColor(GRAY_50)
                        .setBorder(new SolidBorder(GRAY_200, 1f))
                        .setBorderLeft(new SolidBorder(PRIMARY, 3f))
                        .setPaddingTop(14).setPaddingBottom(14)
                        .setPaddingLeft(16).setPaddingRight(16)
                        .setBorderRadius(new BorderRadius(4))
                        .setMarginBottom(20));
            }

            // ================================================================
            // PIECE JOINTE
            // ================================================================
            if (c.getFichierNom() != null) {
                doc.add(sectionTitle("Document joint", bold, regular));
                Table attach = new Table(UnitValue.createPercentArray(new float[]{1, 6}))
                        .setWidth(UnitValue.createPercentValue(100))
                        .setMarginBottom(20)
                        .setBorder(new SolidBorder(GRAY_200, 1f))
                        .setBorderRadius(new BorderRadius(6));

                Cell iconC = new Cell().setBorder(Border.NO_BORDER)
                        .setBackgroundColor(DANGER_LIGHT)
                        .setPadding(16).setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setTextAlignment(TextAlignment.CENTER);
                iconC.add(new Paragraph("PDF")
                        .setFont(bold).setFontSize(11).setFontColor(DANGER)
                        .setTextAlignment(TextAlignment.CENTER));

                Cell nameC = new Cell().setBorder(Border.NO_BORDER)
                        .setBackgroundColor(GRAY_50).setPadding(16)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE);
                nameC.add(new Paragraph(c.getFichierNom())
                        .setFont(bold).setFontSize(10).setFontColor(GRAY_800).setMarginBottom(2));
                nameC.add(new Paragraph("Document attache au courrier")
                        .setFont(regular).setFontSize(8).setFontColor(GRAY_500));

                attach.addCell(iconC);
                attach.addCell(nameC);
                doc.add(attach);
            }

            // ================================================================
            // WORKFLOW / CIRCUIT
            // ================================================================
            if (c.getEtapeCourante() != null && c.getWorkflow() != null) {
                doc.add(sectionTitle("Circuit de traitement", bold, regular));
                Table wfTable = new Table(UnitValue.createPercentArray(new float[]{1}))
                        .setWidth(UnitValue.createPercentValue(100))
                        .setMarginBottom(20)
                        .setBorder(new SolidBorder(GRAY_200, 1f))
                        .setBorderRadius(new BorderRadius(6));

                Cell wfCell = new Cell().setBorder(Border.NO_BORDER)
                        .setBackgroundColor(PRIMARY_LIGHT).setPadding(14);
                wfCell.add(new Paragraph()
                        .add(new Text("Workflow : ").setFont(regular).setFontSize(9).setFontColor(PRIMARY))
                        .add(new Text(c.getWorkflow().getNom()).setFont(bold).setFontSize(10).setFontColor(PRIMARY)));
                wfCell.add(new Paragraph()
                        .add(new Text("Etape courante : ").setFont(regular).setFontSize(9).setFontColor(GRAY_700))
                        .add(new Text(c.getEtapeCourante().getNom()).setFont(bold).setFontSize(10).setFontColor(GRAY_800))
                        .setMarginTop(4));
                wfTable.addCell(wfCell);
                doc.add(wfTable);
            }

            // ================================================================
            // HISTORIQUE DES ACTIONS
            // ================================================================
            List<HistoriqueCourrier> hists = c.getHistoriques();
            if (hists != null && !hists.isEmpty()) {
                doc.add(sectionTitle("Historique des actions (" + hists.size() + ")", bold, regular));

                Table histTable = new Table(UnitValue.createPercentArray(new float[]{20, 18, 22, 40}))
                        .setWidth(UnitValue.createPercentValue(100))
                        .setMarginBottom(24)
                        .setBorderRadius(new BorderRadius(6))
                        .setBorder(new SolidBorder(GRAY_200, 1f));

                // En-tête tableau
                String[] cols = {"DATE", "ACTION", "UTILISATEUR", "COMMENTAIRE"};
                for (String col : cols) {
                    Cell hc = new Cell()
                            .setBackgroundColor(GRAY_800).setBorder(Border.NO_BORDER)
                            .setPaddingTop(10).setPaddingBottom(10)
                            .setPaddingLeft(12).setPaddingRight(12);
                    hc.add(new Paragraph(col)
                            .setFont(bold).setFontSize(8).setFontColor(GRAY_300)
                            .setCharacterSpacing(0.8f));
                    histTable.addHeaderCell(hc);
                }

                // Lignes
                boolean alt = false;
                for (HistoriqueCourrier h : hists) {
                    DeviceRgb bg = alt ? GRAY_50 : WHITE;

                    String dateVal   = h.getDate() != null ? h.getDate().format(FMT) : "-";
                    String actionVal = formatAction(h.getAction().toString());
                    DeviceRgb actionColor = getActionColor(h.getAction().toString());
                    String userVal   = h.getUser() != null
                            ? h.getUser().getPrenom() + " " + h.getUser().getNom() : "-";
                    String commVal   = h.getCommentaire() != null
                            && !h.getCommentaire().isBlank() ? h.getCommentaire() : "-";

                    // Date
                    Cell dc = new Cell().setBackgroundColor(bg).setBorder(Border.NO_BORDER)
                            .setBorderBottom(new SolidBorder(GRAY_100, 0.5f))
                            .setPaddingTop(9).setPaddingBottom(9).setPaddingLeft(12).setPaddingRight(8);
                    dc.add(new Paragraph(dateVal).setFont(regular).setFontSize(8).setFontColor(GRAY_500));
                    histTable.addCell(dc);

                    // Action avec badge couleur
                    Cell ac = new Cell().setBackgroundColor(bg).setBorder(Border.NO_BORDER)
                            .setBorderBottom(new SolidBorder(GRAY_100, 0.5f))
                            .setPaddingTop(9).setPaddingBottom(9).setPaddingLeft(8).setPaddingRight(8)
                            .setVerticalAlignment(VerticalAlignment.MIDDLE);
                    ac.add(new Paragraph()
                            .add(new Text(" " + actionVal + " ")
                                    .setFont(bold).setFontSize(8).setFontColor(WHITE)
                                    .setBackgroundColor(actionColor, 3f, 2f, 3f, 2f)));
                    histTable.addCell(ac);

                    // Utilisateur
                    Cell uc = new Cell().setBackgroundColor(bg).setBorder(Border.NO_BORDER)
                            .setBorderBottom(new SolidBorder(GRAY_100, 0.5f))
                            .setPaddingTop(9).setPaddingBottom(9).setPaddingLeft(8).setPaddingRight(8);
                    uc.add(new Paragraph(userVal).setFont(bold).setFontSize(9).setFontColor(GRAY_700));
                    histTable.addCell(uc);

                    // Commentaire
                    Cell cc = new Cell().setBackgroundColor(bg).setBorder(Border.NO_BORDER)
                            .setBorderBottom(new SolidBorder(GRAY_100, 0.5f))
                            .setPaddingTop(9).setPaddingBottom(9).setPaddingLeft(8).setPaddingRight(12);
                    cc.add(new Paragraph(commVal)
                            .setFont("-".equals(commVal) ? italic : regular)
                            .setFontSize(9)
                            .setFontColor("-".equals(commVal) ? GRAY_300 : GRAY_700));
                    histTable.addCell(cc);

                    alt = !alt;
                }
                doc.add(histTable);
            }

            // ================================================================
            // FOOTER
            // ================================================================
            doc.setMargins(0, 0, 0, 0);

            // Ligne décorative avant footer
            Table footerBar = new Table(UnitValue.createPercentArray(new float[]{1, 6, 1}))
                    .setWidth(UnitValue.createPercentValue(100));
            footerBar.addCell(new Cell().setBorder(Border.NO_BORDER)
                    .setBackgroundColor(PRIMARY).setHeight(4));
            footerBar.addCell(new Cell().setBorder(Border.NO_BORDER)
                    .setBackgroundColor(ACCENT).setHeight(4));
            footerBar.addCell(new Cell().setBorder(Border.NO_BORDER)
                    .setBackgroundColor(PRIMARY).setHeight(4));
            doc.add(footerBar);

            Table footer = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setBackgroundColor(GRAY_900);

            Cell f1 = new Cell().setBorder(Border.NO_BORDER)
                    .setPaddingLeft(32).setPaddingTop(16).setPaddingBottom(16);
            f1.add(new Paragraph()
                    .add(new Text("SAMA COURRIER\n").setFont(bold).setFontSize(10).setFontColor(WHITE))
                    .add(new Text("by magentatechno").setFont(italic).setFontSize(8).setFontColor(GRAY_500)));
            footer.addCell(f1);

            Cell f2 = new Cell().setBorder(Border.NO_BORDER)
                    .setPaddingTop(16).setPaddingBottom(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);
            f2.add(new Paragraph("Document confidentiel - Usage interne")
                    .setFont(italic).setFontSize(8).setFontColor(GRAY_500)
                    .setTextAlignment(TextAlignment.CENTER));
            footer.addCell(f2);

            Cell f3 = new Cell().setBorder(Border.NO_BORDER)
                    .setPaddingRight(32).setPaddingTop(16).setPaddingBottom(16)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);
            f3.add(new Paragraph("Genere le\n")
                    .setFont(regular).setFontSize(7).setFontColor(GRAY_500)
                    .setTextAlignment(TextAlignment.RIGHT).setMarginBottom(0));
            f3.add(new Paragraph(LocalDateTime.now().format(FMT))
                    .setFont(bold).setFontSize(8).setFontColor(GRAY_300)
                    .setTextAlignment(TextAlignment.RIGHT));
            footer.addCell(f3);
            doc.add(footer);

            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur PDF : " + e.getMessage(), e);
        }
    }

    // ================================================================
    // HELPERS
    // ================================================================
    private Paragraph sectionTitle(String text, PdfFont bold, PdfFont regular) {
        return new Paragraph()
                .add(new Text(text.toUpperCase())
                        .setFont(bold).setFontSize(9).setFontColor(PRIMARY)
                        .setCharacterSpacing(1.2f))
                .setMarginBottom(8).setMarginTop(16)
                .setBorderBottom(new SolidBorder(PRIMARY_LIGHT, 1.5f))
                .setPaddingBottom(5);
    }

    private DeviceRgb getStatutColor(String s) {
        if ("VALIDE".equals(s))   return SUCCESS;
        if ("REJETE".equals(s))   return DANGER;
        if ("EN_COURS".equals(s)) return WARNING;
        if ("ARCHIVE".equals(s))  return GRAY_500;
        return INFO;
    }

    private String getStatutLabel(String s) {
        if ("NOUVEAU".equals(s))  return "Nouveau";
        if ("EN_COURS".equals(s)) return "En cours";
        if ("VALIDE".equals(s))   return "Valide";
        if ("REJETE".equals(s))   return "Rejete";
        if ("ARCHIVE".equals(s))  return "Archive";
        return s;
    }

    private DeviceRgb getPrioriteColor(String p) {
        if ("URGENTE".equals(p)) return DANGER;
        if ("HAUTE".equals(p))   return WARNING;
        if ("BASSE".equals(p))   return GRAY_500;
        return INFO;
    }

    private DeviceRgb getActionColor(String a) {
        if ("CREATION".equals(a))    return SUCCESS;
        if ("VALIDATION".equals(a))  return INFO;
        if ("REJET".equals(a))       return DANGER;
        if ("ARCHIVAGE".equals(a))   return GRAY_500;
        if ("TRANSFERT".equals(a))   return WARNING;
        if ("AFFECTATION".equals(a)) return ACCENT;
        return PRIMARY;
    }

    private String formatAction(String a) {
        if ("CREATION".equals(a))     return "Creation";
        if ("AFFECTATION".equals(a))  return "Affectation";
        if ("TRANSFERT".equals(a))    return "Transfert";
        if ("VALIDATION".equals(a))   return "Validation";
        if ("REJET".equals(a))        return "Rejet";
        if ("ARCHIVAGE".equals(a))    return "Archivage";
        if ("MODIFICATION".equals(a)) return "Modification";
        return a;
    }
}
