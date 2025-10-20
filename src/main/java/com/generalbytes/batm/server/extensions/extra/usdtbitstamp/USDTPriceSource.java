package com.generalbytes.batm.server.extensions.extra.usdtbitstamp;

import com.generalbytes.batm.server.extensions.IRateSource;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.util.*;

public class USDTPriceSource implements IRateSource {

    private static final String DEFAULT_FIAT = "USD";

    private static final Map<String, String> CRYPTO_ALIASES;

    static {
        Map<String, String> aliases = new HashMap<>();
        aliases.put("USDT", "USDT");
        aliases.put("USDTTTRON", "USDT");
        aliases.put("USDTTRON", "USDT");
        aliases.put("USDT_TRON", "USDT");
        aliases.put("USDT-TRON", "USDT");
        aliases.put("USDTTTRX", "USDT");
        aliases.put("USDTTRX", "USDT");
        aliases.put("USDT.TRC20", "USDT");
        aliases.put("USDTTRC20", "USDT");
        aliases.put("USDT_TRC20", "USDT");
        aliases.put("USDT-TRC20", "USDT");
        CRYPTO_ALIASES = Collections.unmodifiableMap(aliases);
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        final String normalizedCrypto = normalizeCrypto(cryptoCurrency);
        if (normalizedCrypto == null) {
            return null;
        }

        final String fiat = normalizeFiat(fiatCurrency);
        if (!DEFAULT_FIAT.equals(fiat)) {
            return null;
        }

        try {
            final String pair = normalizedCrypto.toLowerCase(Locale.ROOT) + DEFAULT_FIAT.toLowerCase(Locale.ROOT);
            URL url = new URL("https://www.bitstamp.net/api/v2/ticker/" + pair + "/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "GB-BATM-USDTBitstamp/1.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.err.println("[USDTBitstamp] Unexpected HTTP status: " + conn.getResponseCode());
                return null;
            }

            StringBuilder json = new StringBuilder();
            try (InputStream inputStream = conn.getInputStream();
                 Scanner sc = new Scanner(inputStream)) {
                while (sc.hasNext()) json.append(sc.nextLine());
            }

            // parsing sencillo (Bitstamp devuelve {"last":"1.0000",...})
            String body = json.toString();
            int i = body.indexOf("\"last\":\"");
            if (i < 0) return null;
            int start = i + 8;
            int end = body.indexOf('"', start);
            if (end < 0) return null;

            String priceStr = body.substring(start, end);
            return new BigDecimal(priceStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override public Set<String> getFiatCurrencies() { return Collections.singleton(DEFAULT_FIAT); }
    @Override public String getPreferredFiatCurrency() { return DEFAULT_FIAT; }

    @Override
    public Set<String> getCryptoCurrencies() {
        // Lo que reportamos como soportado
        return new HashSet<>(CRYPTO_ALIASES.keySet());
    }

    private String normalizeCrypto(String cryptoCurrency) {
        if (cryptoCurrency == null) {
            return null;
        }

        return CRYPTO_ALIASES.get(cryptoCurrency.toUpperCase(Locale.ROOT));
    }

    private String normalizeFiat(String fiatCurrency) {
        if (fiatCurrency == null || fiatCurrency.trim().isEmpty()) {
            return DEFAULT_FIAT;
        }

        return fiatCurrency.toUpperCase(Locale.ROOT);
    }
}
