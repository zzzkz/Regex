import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class ExtrairImagens {

    public static void main(String[] args) {
        String userHome = System.getProperty("user.home");
        Path downloadsPath = Paths.get(userHome, "Downloads");
        Path sourcesFile = downloadsPath.resolve("sources.txt");
        Path downloadFolder = downloadsPath.resolve("download");

        try {
            // Cria a pasta "download" se não existir
            if (!Files.exists(downloadFolder)) {
                Files.createDirectory(downloadFolder);
                System.out.println("📁 Pasta criada em: " + downloadFolder.toAbsolutePath());
            }

            // Lê o conteúdo inteiro do arquivo sources.txt
            String htmlContent = new String(Files.readAllBytes(sourcesFile));

            // Regex: captura apenas imagens dos palestrantes
            Pattern pattern = Pattern.compile("<img\\s+[^>]*src=[\"'](/media/static/palestrantes/[^\"']+)[\"']", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(htmlContent);

            List<String> imageUrls = new ArrayList<>();
            while (matcher.find()) {
                imageUrls.add(matcher.group(1));
            }

            System.out.println("🔍 Encontradas " + imageUrls.size() + " imagens de palestrantes.");

            int count = 1;
            String baseUrl = "https://eventos.ifgoiano.edu.br/integra2025/";

            for (String relativeUrl : imageUrls) {
                try {
                    // Monta a URL completa
                    String fullUrl = relativeUrl.startsWith("http") ? relativeUrl : baseUrl + relativeUrl;

                    URL url = new URL(fullUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    // Define cabeçalhos para simular um navegador real
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                    connection.setRequestProperty("Accept", "image/*");
                    connection.setRequestProperty("Referer", baseUrl);
                    connection.connect();

                    // Nome do arquivo
                    String extension = getFileExtension(relativeUrl);
                    String fileName = "palestrante_" + count + extension;
                    Path outputPath = downloadFolder.resolve(fileName);

                    try (InputStream in = connection.getInputStream()) {
                        Files.copy(in, outputPath, StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("✅ Baixado: " + fileName);
                    }

                    connection.disconnect();
                    count++;

                } catch (Exception e) {
                    System.out.println("❌ Erro ao baixar " + relativeUrl + " -> " + e.getMessage());
                }
            }

            System.out.println("\n✔️ Processo finalizado!");

        } catch (IOException e) {
            System.out.println("Erro geral: " + e.getMessage());
        }
    }

    private static String getFileExtension(String url) {
        int lastDot = url.lastIndexOf(".");
        if (lastDot != -1 && lastDot < url.length() - 1) {
            return url.substring(lastDot);
        }
        return ".jpg"; // padrão se não encontrar
    }
}
