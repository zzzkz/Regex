import java.io.*;
import java.net.*;

public class BaixarCodigoFonte {
    public static void main(String[] args) {
        try {
            // URL do site
            String siteURL = "https://eventos.ifgoiano.edu.br/integra2025/";

            // Caminho para a pasta Downloads do usuário
            String downloadPath = System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "sources.txt";

            // Criar URI a partir da URL
            URI uri = new URI(siteURL);

            // Converter URI para URL
            URL url = uri.toURL();

            // Abrir conexão com a URL
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(downloadPath))) {

                // Ler linha por linha e salvar no arquivo
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            System.out.println("Código-fonte salvo em: " + downloadPath);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    

    }
}
