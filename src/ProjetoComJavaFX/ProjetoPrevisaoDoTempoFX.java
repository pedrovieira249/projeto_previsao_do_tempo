package ProjetoComJavaFX;

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
import java.util.HashMap;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.*;


public class ProjetoPrevisaoDoTempoFX extends Application {

    @Override
    public void start(Stage palco) throws Exception {
        // --- Componentes de busca ---
        TextField campoCidade = new TextField();
        campoCidade.setPromptText("Sua cidade");
        Button botaoPrevisaoDoTempo = new Button("Buscar \uD83D\uDD0D");

        // --- Seção localização ---
        Label labelCidade   = new Label("--");
        Label labelDataHora = new Label("--");

        // --- Seção destaque ---
        ImageView icone = new ImageView();
        icone.setFitWidth(64);
        icone.setFitHeight(64);
        Label labelTemp     = new Label("-- °C");
        Label labelCondicao = new Label("--");
        labelTemp.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");

        // --- Labels dos mini cards ---
        Label labelSensacao = new Label("-- °C");
        Label labelUmidade  = new Label("-- %");
        Label labelVento    = new Label("-- km/h");
        Label labelPressao  = new Label("-- mb");

        // --- Ação do botão (acessa todos os labels acima) ---
        botaoPrevisaoDoTempo.setOnAction(e -> {
            try {
                String cidade = campoCidade.getText().trim();
                // Documentação da API: https://www.weatherapi.com/docs/
                String dadosClimaticos = getDadosClimaticos(cidade); // Retorna um JSON

                // Valida se houve algum erro ao consultar a API do Weather
                String mensagemErroApi = getMensagemErroApi(dadosClimaticos);
                if (mensagemErroApi != null) {
                    labelCidade.setText("Erro: " + mensagemErroApi);
                    labelCidade.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    return;
                }

                HashMap<String, Object> dados = normalizarDadosClimaticos(dadosClimaticos);

                labelCidade.setText(dados.get("cidade") + "/" + dados.get("estado") + ", " + dados.get("pais"));
                labelDataHora.setText((String) dados.get("dataHora"));
                labelTemp.setText(dados.get("temperatura") + " °C");
                labelCondicao.setText((String) dados.get("condicaoDoTempo"));
                icone.setImage(new Image((String) dados.get("iconUrl")));
                labelSensacao.setText(dados.get("sensacaoTermica") + " °C");
                labelUmidade.setText(dados.get("umidade") + "%");
                labelVento.setText(dados.get("velocidadeDoVento") + " km/h");
                labelPressao.setText(dados.get("pressaoAtmosferica") + " mb");
            } catch (Exception ex) {
                labelCidade.setText("Erro: " + ex.getMessage());
            }
        });

        // --- Montagem dos containers ---
        HBox barraBusca = new HBox(10, campoCidade, botaoPrevisaoDoTempo);
        barraBusca.setPadding(new Insets(10));
        HBox.setHgrow(campoCidade, Priority.ALWAYS);

        VBox secaoLocalizacao = new VBox(5, labelCidade, labelDataHora);
        secaoLocalizacao.setPadding(new Insets(10));

        VBox secaoDestaque = new VBox(5, icone, labelTemp, labelCondicao);
        secaoDestaque.setAlignment(Pos.CENTER);
        secaoDestaque.setPadding(new Insets(10));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        ColumnConstraints coluna = new ColumnConstraints();
        coluna.setHgrow(Priority.ALWAYS);
        coluna.setFillWidth(true);
        grid.getColumnConstraints().addAll(coluna, coluna);

        grid.add(criarCard("Sensacao",  labelSensacao), 0, 0);
        grid.add(criarCard("Umidade",   labelUmidade),  1, 0);
        grid.add(criarCard("Vento",     labelVento),    0, 1);
        grid.add(criarCard("Pressao",   labelPressao),  1, 1);

        VBox layout = new VBox(10, barraBusca, secaoLocalizacao, secaoDestaque, grid);
        layout.setPadding(new Insets(15));

        // Cena e palco
        Scene cena = new Scene(layout, 600, 500);
        palco.setTitle("Previsão do Tempo");
        palco.setScene(cena);
        palco.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private VBox criarCard(String titulo, Label labelValor) {
        Label labelTitulo = new Label(titulo);
        VBox card = new VBox(5, labelTitulo, labelValor);
        card.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 8; -fx-padding: 10; -fx-background-radius: 8;");
        return card;
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

    public static HashMap<String, Object> normalizarDadosClimaticos(String dadosClimaticos)
    {
        JSONObject jsonDadosClimaticos = new JSONObject(dadosClimaticos);
        // Ná doc da API ná sessão de "Bulk Request" tem as informações de Response
        JSONObject informacoesMeteorologicas = jsonDadosClimaticos.getJSONObject("current");

        String cidade = jsonDadosClimaticos.getJSONObject("location").getString("name");
        String estado = jsonDadosClimaticos.getJSONObject("location").getString("region");
        String pais = jsonDadosClimaticos.getJSONObject("location").getString("country");
        String dataHora = toBrazilianFormat(jsonDadosClimaticos.getJSONObject("location").getString("localtime"));
        Float temperatura = informacoesMeteorologicas.optFloat("temp_c");
        Float sensacaoTermica = informacoesMeteorologicas.optFloat("feelslike_c");
        String condicaoDoTempo = traduzirCondicaoDoTempo(informacoesMeteorologicas.getJSONObject("condition").optString("text"));
        String iconUrl = "https:" + informacoesMeteorologicas.getJSONObject("condition").optString("icon");
        Integer umidade = informacoesMeteorologicas.optInt("humidity");
        Float velocidadeDoVendo = informacoesMeteorologicas.optFloat("wind_kph");
        Float pressaoAtmosferica = informacoesMeteorologicas.optFloat("pressure_mb");

        HashMap<String, Object> dadosClimaticosFormatados = new HashMap<>();
        dadosClimaticosFormatados.put("cidade",             cidade);
        dadosClimaticosFormatados.put("estado",             estado);
        dadosClimaticosFormatados.put("pais",               pais);
        dadosClimaticosFormatados.put("dataHora",           dataHora);
        dadosClimaticosFormatados.put("temperatura",        temperatura);
        dadosClimaticosFormatados.put("sensacaoTermica",    sensacaoTermica);
        dadosClimaticosFormatados.put("condicaoDoTempo",    condicaoDoTempo);
        dadosClimaticosFormatados.put("iconUrl",            iconUrl);
        dadosClimaticosFormatados.put("umidade",            umidade);
        dadosClimaticosFormatados.put("velocidadeDoVento",  velocidadeDoVendo);
        dadosClimaticosFormatados.put("pressaoAtmosferica", pressaoAtmosferica);

        return dadosClimaticosFormatados;
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