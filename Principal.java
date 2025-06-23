import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class CatalogoLivros {

    static class Livro {
        String titulo;
        String autor;
        List<String> idiomas;
        int downloads;

        public Livro(String titulo, String autor, List<String> idiomas, int downloads) {
            this.titulo = titulo;
            this.autor = autor;
            this.idiomas = idiomas;
            this.downloads = downloads;
        }

        @Override
        public String toString() {
            return "\nTítulo: " + titulo +
                    "\nAutor: " + autor +
                    "\nIdiomas: " + idiomas +
                    "\nDownloads: " + downloads + "\n";
        }
    }

    static List<Livro> catalogo = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("===== Catálogo de Livros =====");
            System.out.println("1. Buscar livro por título");
            System.out.println("2. Listar todos os livros no catálogo");
            System.out.println("3. Remover livro do catálogo");
            System.out.println("4. Limpar o catálogo");
            System.out.println("5. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); // limpar o buffer

            switch (opcao) {
                case 1 -> buscarLivro(scanner);
                case 2 -> listarCatalogo();
                case 3 -> removerLivro(scanner);
                case 4 -> limparCatalogo();
                case 5 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida.");
            }

        } while (opcao != 5);

        scanner.close();
    }

    private static void buscarLivro(Scanner scanner) throws Exception {
        System.out.print("Digite o título do livro para buscar: ");
        String tituloBusca = scanner.nextLine();

        String url = "https://gutendex.com/books?search=" + tituloBusca.replace(" ", "%20");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Gson gson = new Gson();

        JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
        JsonArray results = jsonResponse.getAsJsonArray("results");

        if (results.size() > 0) {
            JsonObject livroJson = results.get(0).getAsJsonObject();

            String titulo = livroJson.get("title").getAsString();
            JsonArray autoresArray = livroJson.getAsJsonArray("authors");
            String autor = autoresArray.size() > 0 ?
                    autoresArray.get(0).getAsJsonObject().get("name").getAsString() :
                    "Autor desconhecido";

            JsonArray idiomasArray = livroJson.getAsJsonArray("languages");
            List<String> idiomas = new ArrayList<>();
            idiomasArray.forEach(lang -> idiomas.add(lang.getAsString()));

            int downloads = livroJson.get("download_count").getAsInt();

            Livro livro = new Livro(titulo, autor, idiomas, downloads);
            catalogo.add(livro);

            System.out.println("Livro adicionado ao catálogo:");
            System.out.println(livro);
        } else {
            System.out.println("Nenhum livro encontrado com esse título.");
        }
    }

    private static void listarCatalogo() {
        if (catalogo.isEmpty()) {
            System.out.println("O catálogo está vazio.");
        } else {
            System.out.println("===== Livros no Catálogo =====");
            for (int i = 0; i < catalogo.size(); i++) {
                System.out.println("Livro " + (i + 1) + ": " + catalogo.get(i));
            }
        }
    }

    private static void removerLivro(Scanner scanner) {
        listarCatalogo();
        if (!catalogo.isEmpty()) {
            System.out.print("Digite o número do livro que deseja remover: ");
            int index = scanner.nextInt();
            scanner.nextLine(); // limpar buffer

            if (index > 0 && index <= catalogo.size()) {
                Livro removido = catalogo.remove(index - 1);
                System.out.println("Livro removido: " + removido.titulo);
            } else {
                System.out.println("Número inválido.");
            }
        }
    }

    private static void limparCatalogo() {
        catalogo.clear();
        System.out.println("Catálogo limpo.");
    }
}
