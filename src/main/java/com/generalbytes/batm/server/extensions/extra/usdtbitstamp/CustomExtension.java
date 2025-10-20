package com.generalbytes.batm.server.extensions.extra.usdtbitstamp;

import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.aml.IAMLProvider;
import com.generalbytes.batm.server.extensions.aml.IExternalIdentityProvider;
import com.generalbytes.batm.server.extensions.aml.scoring.ITransactionScoringProvider;
import com.generalbytes.batm.server.extensions.aml.verification.IIdentityVerificationProvider;
import com.generalbytes.batm.server.extensions.chat.IChatCommand;
import com.generalbytes.batm.server.extensions.communication.ICommunicationProvider;
import com.generalbytes.batm.server.extensions.communication.IPhoneLookupProvider;
import com.generalbytes.batm.server.extensions.communication.voicecall.IVoiceCallProvider;
import com.generalbytes.batm.server.extensions.travelrule.IWalletTypeEvaluationProvider;
import com.generalbytes.batm.server.extensions.watchlist.IWatchList;

import java.math.BigDecimal;
import java.util.*;

public class CustomExtension implements IExtension {

    @Override
    public String getName() {
        return "USDT Bitstamp Rate Source";
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin == null) {
            return null;
        }

        final String trimmedLogin = sourceLogin.trim();
        if (trimmedLogin.isEmpty()) {
            return null;
        }

        final String[] parts = trimmedLogin.split(":", -1);
        if (parts.length == 0 || !parts[0].equalsIgnoreCase("usdtbitstamp")) {
            return null;
        }

        String fiat = null;
        BigDecimal margin = BigDecimal.ZERO;

        if (parts.length > 1) {
            String fiatPart = parts[1].trim();
            if (!fiatPart.isEmpty()) {
                fiat = fiatPart;
            }
        }

        if (parts.length > 2) {
            String marginPart = parts[2].trim();
            if (marginPart.isEmpty()) {
                marginPart = "0";
            }
            try {
                margin = new BigDecimal(marginPart);
            } catch (NumberFormatException e) {
                System.err.println("[USDTBitstamp] Margin inválido '" + parts[2] + "', usando 0");
            }
        }

        System.out.println("[USDTBitstamp] createRateSource login=" + sourceLogin +
                ", fiat=" + (fiat == null ? "(default)" : fiat) +
                ", margin=" + margin);

        return new USDTPriceSource(fiat, margin);
    }

    // En builds recientes CAS usa esto para filtrar lo que muestra en los dropdowns
    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        return new HashSet<>(Arrays.asList(
                "USDT",
                "USDTTTRON",  // 3T (como en tu UI)
                "USDTTRON",   // 2T (otras builds)
                "USDT_TRON",
                "USDT-TRON",
                "USDTTTRX",   // algunos setups raros
                "USDTTRX",
                "USDT.TRC20",
                "USDTTRC20",
                "USDT_TRC20",
                "USDT-TRC20"
        ));
    }

    // ---- resto sin implementar, retornos vacíos/NULL seguros ----
    @Override public Set<IWalletTypeEvaluationProvider> getWalletTypeEvaluationProviders() { return Collections.emptySet(); }
    @Override public IIdentityVerificationProvider createIdentityVerificationProvider(String p, String k) { return null; }
    @Override public Set<ISsnValidator> getSsnValidators() { return Collections.emptySet(); }
    @Override public Set<IVoiceCallProvider> getVoiceCallProviders() { return Collections.emptySet(); }
    @Override public Set<ICommunicationProvider> getCommunicationProviders() { return Collections.emptySet(); }
    @Override public ITransactionScoringProvider createTransactionScoringProvider(String id) { return null; }
    @Override public Set<IPhoneLookupProvider> getPhoneLookupProviders() { return Collections.emptySet(); }
    @Override public Set<IAMLProvider> getAMLProviders() { return Collections.emptySet(); }
    @Override public Set<IExternalIdentityProvider> getIdentityProviders() { return Collections.emptySet(); }
    @Override public Set<Class> getChatCommands() { return Collections.emptySet(); }
    @Override public Set<IRestService> getRestServices() { return Collections.emptySet(); }
    @Override public IWatchList getWatchList(String name) { return null; }
    @Override public Set<String> getSupportedWatchListsNames() { return Collections.emptySet(); }
    @Override public IPaperWalletGenerator createPaperWalletGenerator(String c) { return null; }
    @Override public ICryptoAddressValidator createAddressValidator(String c) { return null; }
    @Override public IWallet createWallet(String w, String t) { return null; }
    @Override public IPaymentProcessor createPaymentProcessor(String p) { return null; }
    @Override public IExchange createExchange(String e) { return null; }
    @Override public Set<ICryptoCurrencyDefinition> getCryptoCurrencyDefinitions() { return Collections.emptySet(); }
    @Override public void deinit() { }
    @Override public void init(IExtensionContext context) { }
}
