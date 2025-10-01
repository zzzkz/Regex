import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.*;
import java.util.*;

public class ExtrairPalestrantes {
    // remove tags residuais e normaliza espaços
    private static String clean(String s) {
        if (s == null) return "";
        // remove tags html simples
        s = s.replaceAll("<br\\s*/?>", " ");
        s = s.replaceAll("<[^>]+>", "");
        // normaliza espaços
        return s.replaceAll("\\s+", " ").trim();
    }

    public static void main(String[] args) {
        try {
            String caminho = System.getProperty("user.home") + "/Downloads/sources.txt";
            String html = Files.readString(Paths.get(caminho));

            // h4 com quaisquer atributos (DOTALL para pegar com quebras de linha)
            Pattern pNome   = Pattern.compile("<h4[^>]*>(.*?)</h4>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            Pattern pLocal  = Pattern.compile("<h6[^>]*>(.*?)</h6>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            Pattern pContato= Pattern.compile("<div\\s+class=\"modal-body\">\\s*.*?<p>(.*?)</p>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            // prioriza imagens da pasta /palestrantes
            Pattern pImgPref= Pattern.compile("<img\\s+src=\"([^\"]*palestrantes[^\"]*)\"", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

            List<Matcher> nomes = new ArrayList<>();
            Matcher mNome = pNome.matcher(html);
            while (mNome.find()) nomes.add(pNome.matcher(html).region(mNome.start(), mNome.end())); // só pra manter índices
            // vamos armazenar os matches reais com índices também:
            List<int[]> idxs = new ArrayList<>();
            List<String> nomesTxt = new ArrayList<>();
            mNome.reset();
            while (mNome.find()) {
                idxs.add(new int[]{mNome.start(), mNome.end()});
                nomesTxt.add(clean(mNome.group(1)));
            }

            for (int i = 0; i < idxs.size(); i++) {
                String nome = nomesTxt.get(i);

                // pula títulos que não são palestrantes
                if (nome.endsWith(":") || nome.equals(nome.toUpperCase())) {
                    continue;
                }

                int startH4 = idxs.get(i)[0];
                int endH4   = idxs.get(i)[1];
                int nextH4Start = (i + 1 < idxs.size()) ? idxs.get(i + 1)[0] : html.length();

                // bloco à frente do nome (para local e contato)
                String forwardBlock = html.substring(endH4, nextH4Start);

                // busca local e contato dentro do bloco à frente
                String local = "";
                Matcher mLocal = pLocal.matcher(forwardBlock);
                if (mLocal.find()) local = clean(mLocal.group(1));

                String contato = "";
                Matcher mContato = pContato.matcher(forwardBlock);
                if (mContato.find()) contato = clean(mContato.group(1));

                // janela para trás para achar a imagem do palestrante:
                // olha até 4000 chars antes do <h4>, mas não passa do <h4> anterior
                int prevH4Start = (i - 1 >= 0) ? idxs.get(i - 1)[0] : 0;
                int backStart = Math.max(prevH4Start, startH4 - 4000);
                String backBlock = html.substring(backStart, startH4);

                // pega a ÚLTIMA imagem de /palestrantes antes do nome
                String imagem = "";
                Matcher mImgBack = pImgPref.matcher(backBlock);
                int lastStart = -1;
                while (mImgBack.find()) {
                    imagem = mImgBack.group(1).trim();
                    lastStart = mImgBack.start();
                }
                // fallback: se não encontrou antes, tenta no bloco à frente
                if (imagem.isEmpty()) {
                    Matcher mImgFwd = pImgPref.matcher(forwardBlock);
                    if (mImgFwd.find()) imagem = mImgFwd.group(1).trim();
                }

                // imprime apenas se tiver algum dado útil do card
                if (!local.isEmpty() || !contato.isEmpty() || !imagem.isEmpty()) {
                    System.out.println("Palestrante: " + nome);
                    System.out.println("Local de Trabalho: " + local);
                    System.out.println("Contato: " + contato);
                    System.out.println("Imagem: " + imagem);
                    System.out.println("---------------");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
