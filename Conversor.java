import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Conversor {

    // 👉 Substitua com sua chave da ExchangeRate-API
    private static final String API_KEY = "e2546a89a3277509e550b5c0";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String[] currencies = {"USD", "EUR", "BRL", "GBP", "JPY", "AUD"};

        System.out.println("Conversor de Moedas - ExchangeRate-API");
        System.out.print("Digite o valor: ");
        double amount = scanner.nextDouble();

        System.out.print("De (USD, EUR, BRL, GBP, JPY, AUD): ");
        String fromCurrency = scanner.next().toUpperCase();

        System.out.print("Para (USD, EUR, BRL, GBP, JPY, AUD): ");
        String toCurrency = scanner.next().toUpperCase();

        if (!isValidCurrency(fromCurrency, currencies) || !isValidCurrency(toCurrency, currencies)) {
            System.out.println("Moeda inválida.");
            return;
        }

        convertCurrency(fromCurrency, toCurrency, amount);
    }

    public static boolean isValidCurrency(String currency, String[] supportedCurrencies) {
        for (String c : supportedCurrencies) {
            if (c.equalsIgnoreCase(currency)) return true;
        }
        return false;
    }

    public static void convertCurrency(String from, String to, double amount) {
        try {
            // URL correta da API para obter taxas de câmbio
            String url = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/" + from;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse JSON
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response.body(), JsonObject.class);

            if (jsonObject.has("conversion_rates")) {
                JsonObject rates = jsonObject.getAsJsonObject("conversion_rates");

                if (rates.has(to)) {
                    double rate = rates.get(to).getAsDouble();
                    double convertedAmount = amount * rate;

                    System.out.printf("%.2f %s = %.2f %s%n", amount, from, convertedAmount, to);
                } else {
                    System.out.println("Moeda de destino não encontrada.");
                }
            } else {
                System.out.println("Erro ao obter taxas de câmbio.");
            }

        } catch (Exception e) {
            System.out.println("Erro na conversão: " + e.getMessage());
        }
    }
}
