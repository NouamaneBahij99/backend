package com.magentatechno.pelican.service;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfService {

    public byte[] generateCourrierPdf(Object courrier) {
        try {
            String numero = getValue(courrier, "getNumero");
            String objet = getValue(courrier, "getObjet");
            String expediteur = getValue(courrier, "getExpediteur");
            String destinataire = getValue(courrier, "getDestinataire");
            String type = getValue(courrier, "getType");
            String statut = getValue(courrier, "getStatut");
            String priorite = getValue(courrier, "getPriorite");
            String contenu = getValue(courrier, "getContenu");
            String createdAt = getValue(courrier, "getCreatedAt");

            List<String> lines = new ArrayList<>();

            lines.add("PELICAN - GESTION DU COURRIER");
            lines.add("by magentatechno");
            lines.add("");
            lines.add("COURRIER N: " + numero);
            lines.add("");
            lines.add("INFORMATIONS DU COURRIER");
            lines.add("Numero       : " + numero);
            lines.add("Objet        : " + objet);
            lines.add("Type         : " + type);
            lines.add("Statut       : " + statut);
            lines.add("Priorite     : " + priorite);
            lines.add("Date         : " + createdAt);
            lines.add("");
            lines.add("PARTIES CONCERNEES");
            lines.add("Expediteur   : " + expediteur);
            lines.add("Destinataire : " + destinataire);
            lines.add("");
            lines.add("CONTENU / RESUME");

            if (contenu == null || contenu.equals("N/A") || contenu.isBlank()) {
                lines.add("Aucun contenu renseigne.");
            } else {
                lines.addAll(wrapText(contenu, 85));
            }

            lines.add("");
            lines.add("Document genere le " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            lines.add("Systeme Pelican - 2026 Magentatechno");

            return buildSimplePdf(lines);

        } catch (Exception e) {
            e.printStackTrace();
            return buildSimplePdf(List.of(
                    "PELICAN - GESTION DU COURRIER",
                    "",
                    "Erreur lors de la generation du PDF.",
                    "Message: " + e.getMessage()
            ));
        }
    }

    private String getValue(Object obj, String methodName) {
        try {
            Object value = obj.getClass().getMethod(methodName).invoke(obj);
            if (value == null) return "N/A";
            return cleanText(value.toString());
        } catch (Exception e) {
            return "N/A";
        }
    }

    private String cleanText(String text) {
        if (text == null) return "N/A";

        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        normalized = normalized
                .replace("’", "'")
                .replace("–", "-")
                .replace("—", "-")
                .replace("œ", "oe")
                .replace("Œ", "OE");

        return normalized.replaceAll("[^\\x20-\\x7E]", " ");
    }

    private List<String> wrapText(String text, int maxLength) {
        List<String> result = new ArrayList<>();
        String[] words = cleanText(text).split("\\s+");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            if (line.length() + word.length() + 1 > maxLength) {
                result.add(line.toString());
                line = new StringBuilder(word);
            } else {
                if (line.length() > 0) line.append(" ");
                line.append(word);
            }
        }

        if (line.length() > 0) {
            result.add(line.toString());
        }

        return result;
    }

    private byte[] buildSimplePdf(List<String> lines) {
        try {
            StringBuilder content = new StringBuilder();

            content.append("BT\n");

            int y = 800;

            for (int i = 0; i < lines.size(); i++) {
                String line = escapePdf(lines.get(i));

                if (i == 0) {
                    content.append("/F2 18 Tf\n");
                } else if (
                        line.equals("INFORMATIONS DU COURRIER") ||
                        line.equals("PARTIES CONCERNEES") ||
                        line.equals("CONTENU / RESUME")
                ) {
                    content.append("/F2 13 Tf\n");
                } else {
                    content.append("/F1 11 Tf\n");
                }

                content.append("50 ").append(y).append(" Td\n");
                content.append("(").append(line).append(") Tj\n");
                content.append("-50 0 Td\n");

                y -= 18;

                if (y < 60) {
                    break;
                }
            }

            content.append("ET\n");

            byte[] contentBytes = content.toString().getBytes(StandardCharsets.ISO_8859_1);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            List<Integer> offsets = new ArrayList<>();

            write(out, "%PDF-1.4\n");

            offsets.add(out.size());
            write(out, "1 0 obj\n");
            write(out, "<< /Type /Catalog /Pages 2 0 R >>\n");
            write(out, "endobj\n");

            offsets.add(out.size());
            write(out, "2 0 obj\n");
            write(out, "<< /Type /Pages /Kids [3 0 R] /Count 1 >>\n");
            write(out, "endobj\n");

            offsets.add(out.size());
            write(out, "3 0 obj\n");
            write(out, "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] ");
            write(out, "/Resources << /Font << /F1 4 0 R /F2 5 0 R >> >> ");
            write(out, "/Contents 6 0 R >>\n");
            write(out, "endobj\n");

            offsets.add(out.size());
            write(out, "4 0 obj\n");
            write(out, "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\n");
            write(out, "endobj\n");

            offsets.add(out.size());
            write(out, "5 0 obj\n");
            write(out, "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >>\n");
            write(out, "endobj\n");

            offsets.add(out.size());
            write(out, "6 0 obj\n");
            write(out, "<< /Length " + contentBytes.length + " >>\n");
            write(out, "stream\n");
            out.write(contentBytes);
            write(out, "endstream\n");
            write(out, "endobj\n");

            int xrefStart = out.size();

            write(out, "xref\n");
            write(out, "0 7\n");
            write(out, "0000000000 65535 f \n");

            for (Integer offset : offsets) {
                write(out, String.format("%010d 00000 n \n", offset));
            }

            write(out, "trailer\n");
            write(out, "<< /Size 7 /Root 1 0 R >>\n");
            write(out, "startxref\n");
            write(out, String.valueOf(xrefStart));
            write(out, "\n%%EOF");

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur generation PDF", e);
        }
    }

    private void write(ByteArrayOutputStream out, String value) throws Exception {
        out.write(value.getBytes(StandardCharsets.ISO_8859_1));
    }

    private String escapePdf(String text) {
        if (text == null) return "";
        return cleanText(text)
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)");
    }
}
