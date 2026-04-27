import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ProjetoPrevisaoDoTempo {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("Digite o nome da cidade: ");
        String cidade = input.nextLine(); // Pega tudo o texto foi digitado pelo usuário

        try {
            // Documentação da API: https://www.weatherapi.com/docs/
            String dadosClimaticos = getDadosClimaticos(cidade); // Retorna um JSON

            // Valida se ouve algum erro ao consultar a API do Weather
            String mensagemErroApi = getMensagemErroApi(dadosClimaticos);
            if (mensagemErroApi != null) {
                System.out.println(mensagemErroApi);
            }

            imprimirDadosClimaticos(dadosClimaticos);
        } catch ( Exception e ) {
            System.out.println("Erro ao tentar consultar a API Weather: " + e.getMessage());
        }
    }

    private static String getDadosClimaticos(String cidade) throws RuntimeException
    {
        String dadosClimaticos = "";

        try {
            String nomeCidadeFormatado = URLEncoder.encode(cidade, StandardCharsets.UTF_8);
            String apiKey = Files.readString(Paths.get("src/APIKEY.txt")).trim();
            String apiUrl = "http://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=" + nomeCidadeFormatado;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();

            try (HttpClient client = HttpClient.newHttpClient()) {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                dadosClimaticos = response.body();
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return dadosClimaticos;
    }

    public static String getMensagemErroApi(String dadosClimaticos) {
        try {
            JSONObject json = new JSONObject(dadosClimaticos);
            JSONObject erro = json.optJSONObject("error");

            if (erro == null) {
                return null;
            }

            int codigoErro = erro.optInt("code", -1);
            String mensagemApi = erro.optString("message", "Erro não detalhado pela API.");

            String descricaoErro = switch (codigoErro) {
                case 1002 -> "A chave da API não foi informada.";
                case 1003 -> "O parâmetro 'q' não foi informado.";
                case 1005 -> "A URL da requisição da API é inválida.";
                case 1006 -> "Nenhuma localização foi encontrada para o valor informado em 'q'.";
                case 2006 -> "A chave da API informada é inválida.";
                case 2007 -> "A chave da API excedeu a cota mensal de chamadas.";
                case 2008 -> "A chave da API foi desabilitada.";
                case 2009 -> "A chave da API não tem acesso a este recurso no plano atual.";
                case 9000 -> "O JSON enviado na requisição em lote é inválido ou não está em UTF-8.";
                case 9001 -> "O JSON enviado na requisição em lote contém mais de 50 localizações.";
                case 9999 -> "Erro interno da aplicação da API.";
                default -> null;
            };

            if (descricaoErro != null) {
                return "Erro da API WeatherAPI (" + codigoErro + "): " + descricaoErro;
            }

            return "Erro retornado pela API WeatherAPI (" + codigoErro + "): " + mensagemApi;
        } catch (Exception e) {
            return null;
        }
    }

    public static void imprimirDadosClimaticos(String dadosClimaticos)
    {
        JSONObject jsonDadosClimaticos = new JSONObject(dadosClimaticos);
        // Ná doc da API ná sessão de "Bulk Request" tem as informações de Response
        JSONObject informacoesMeteorologicas = jsonDadosClimaticos.getJSONObject("current");

        String cidade = jsonDadosClimaticos.getJSONObject("location").getString("name");
        String estado = jsonDadosClimaticos.getJSONObject("location").getString("region");
        String pais = jsonDadosClimaticos.getJSONObject("location").getString("country");
        String dataHora = toBrazilianFormat(jsonDadosClimaticos.getJSONObject("location").getString("localtime"));

        float temperatura = informacoesMeteorologicas.optFloat("temp_c");
        float sensacaoTermica = informacoesMeteorologicas.optFloat("feelslike_c");
        String condicaoDoTempo = informacoesMeteorologicas.getJSONObject("condition").optString("text");
        int umidade = informacoesMeteorologicas.optInt("humidity");
        float velocidadeDoVendo = informacoesMeteorologicas.optFloat("wind_kph");
        float pressaoAtmosferica = informacoesMeteorologicas.optFloat("pressure_mb");

        System.out.println("Informações Meteorológicas para " + cidade + "/" + estado + ", no " + pais);
        System.out.println("Data e Hora: " + dataHora);
        System.out.println("Temperatura Atual: " + temperatura + " °C");
        System.out.println("Sensação Térmica: " + sensacaoTermica+ " °C");
        System.out.println("Condição do Tempo: " + condicaoDoTempo);
        System.out.println("Umidade: " + umidade + "%");
        System.out.println("Velocidade do Vendo: " + velocidadeDoVendo + " km/h");
        System.out.println("Pressão Atmosférica: " + pressaoAtmosferica + " mb");
    }

    public static String toBrazilianFormat(String dataHora)
    {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        LocalDateTime dateTime = LocalDateTime.parse(dataHora, inputFormatter);
        return dateTime.format(outputFormatter);
    }
}