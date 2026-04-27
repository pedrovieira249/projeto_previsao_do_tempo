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
        String dadosClimaticos;

        try {
            String nomeCidadeFormatado = URLEncoder.encode(cidade, StandardCharsets.UTF_8);
            String apiKey = Files.readString(Paths.get("src/APIKEY.txt")).trim();
            String apiUrl = "https://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=" + nomeCidadeFormatado;

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
        String condicaoDoTempo = traduzirCondicaoDoTempo(informacoesMeteorologicas.getJSONObject("condition").optString("text"));
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

    public static String traduzirCondicaoDoTempo(String condicao) {
        return switch (condicao) {
            case "Sunny"                                        -> "Ensolarado";
            case "Clear"                                        -> "Céu limpo";
            case "Partly cloudy"                                -> "Parcialmente nublado";
            case "Cloudy"                                       -> "Nublado";
            case "Overcast"                                     -> "Encoberto";
            case "Mist"                                         -> "Névoa";
            case "Patchy rain possible"                         -> "Possibilidade de chuva irregular";
            case "Patchy snow possible"                         -> "Possibilidade de neve irregular";
            case "Patchy sleet possible"                        -> "Possibilidade de granizo irregular";
            case "Patchy freezing drizzle possible"             -> "Possibilidade de garoa congelante irregular";
            case "Thundery outbreaks possible"                  -> "Possibilidade de trovoadas";
            case "Blowing snow"                                 -> "Neve com vento";
            case "Blizzard"                                     -> "Nevasca";
            case "Fog"                                          -> "Neblina";
            case "Freezing fog"                                 -> "Neblina congelante";
            case "Patchy light drizzle"                         -> "Garoa leve irregular";
            case "Light drizzle"                                -> "Garoa leve";
            case "Freezing drizzle"                             -> "Garoa congelante";
            case "Heavy freezing drizzle"                       -> "Garoa congelante intensa";
            case "Patchy light rain"                            -> "Chuva leve irregular";
            case "Light rain"                                   -> "Chuva leve";
            case "Moderate rain at times"                       -> "Chuva moderada às vezes";
            case "Moderate rain"                                -> "Chuva moderada";
            case "Heavy rain at times"                          -> "Chuva forte às vezes";
            case "Heavy rain"                                   -> "Chuva forte";
            case "Light freezing rain"                          -> "Chuva congelante leve";
            case "Moderate or heavy freezing rain"              -> "Chuva congelante moderada ou forte";
            case "Light sleet"                                  -> "Granizo leve";
            case "Moderate or heavy sleet"                      -> "Granizo moderado ou forte";
            case "Patchy light snow"                            -> "Neve leve irregular";
            case "Light snow"                                   -> "Neve leve";
            case "Patchy moderate snow"                         -> "Neve moderada irregular";
            case "Moderate snow"                                -> "Neve moderada";
            case "Patchy heavy snow"                            -> "Neve intensa irregular";
            case "Heavy snow"                                   -> "Neve intensa";
            case "Ice pellets"                                  -> "Pelotas de gelo";
            case "Light rain shower"                            -> "Pancada de chuva leve";
            case "Moderate or heavy rain shower"                -> "Pancada de chuva moderada ou forte";
            case "Torrential rain shower"                       -> "Pancada de chuva torrencial";
            case "Light sleet showers"                          -> "Pancadas de granizo leve";
            case "Moderate or heavy sleet showers"              -> "Pancadas de granizo moderado ou forte";
            case "Light snow showers"                           -> "Pancadas de neve leve";
            case "Moderate or heavy snow showers"               -> "Pancadas de neve moderada ou forte";
            case "Light showers of ice pellets"                 -> "Pancadas leves de pelotas de gelo";
            case "Moderate or heavy showers of ice pellets"     -> "Pancadas moderadas ou fortes de pelotas de gelo";
            case "Patchy light rain with thunder"               -> "Chuva leve irregular com trovões";
            case "Moderate or heavy rain with thunder"          -> "Chuva moderada ou forte com trovões";
            case "Patchy light snow with thunder"               -> "Neve leve irregular com trovões";
            case "Moderate or heavy snow with thunder"          -> "Neve moderada ou forte com trovões";
            default                                             -> condicao;
        };
    }

    public static String toBrazilianFormat(String dataHora)
    {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        LocalDateTime dateTime = LocalDateTime.parse(dataHora, inputFormatter);
        return dateTime.format(outputFormatter);
    }
}