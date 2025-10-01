public class Main {
    public static void main(String[] args) {
        try {
            BaixarCodigoFonte.main(new String[0]); // executa primeiro

            ExtrairPalestrantes.main(new String[0]); // executa depois

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
