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

import java.util.*;

public class CustomExtension implements IExtension {

    @Override
    public String getName() {
        return "USDT Bitstamp Rate Source";
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin == null) return null;
        final String sl = sourceLogin.toLowerCase(Locale.ROOT);
        // admite "usdtbitstamp" y variantes con parámetros (p.ej. usdtbitstamp:USD)
        if (sl.startsWith("usdtbitstamp")) {
            System.out.println("[USDTBitstamp] createRateSource login=" + sourceLogin);
            return new USDTPriceSource();
        }
        return null;
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
                "USDTTRX"
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
