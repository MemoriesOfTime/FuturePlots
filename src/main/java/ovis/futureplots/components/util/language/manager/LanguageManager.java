/*
 * Copyright 2024 tim03we, Ovis Development
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package ovis.futureplots.components.util.language.manager;

import lombok.Getter;
import lombok.Setter;
import ovis.futureplots.FuturePlots;
import ovis.futureplots.components.util.language.provider.LanguageProvider;
import ovis.futureplots.components.util.language.handler.MainHandler;
import ovis.futureplots.components.util.language.TranslationKey;

import java.util.UUID;

/**
 * @author  Tim tim03we, Ovis Development (2024)
 */
public class LanguageManager {

    @Getter
    @Setter
    private static LanguageProvider handler;

    public static LanguageProvider init() {
        if(handler == null) {
            handler = new MainHandler();
        }
        return handler;
    }

    private String locale;

    public LanguageManager(String locale) {
        this.locale = locale;
    }

    /*
    *
    * The UUID only has a use for custom handlers to customize the player language
    *
     */

    public String message(TranslationKey translationKey, Object... replacements) {
        return message(null, translationKey.getKey(), replacements);
    }

    public String message(String key, Object... replacements) {
        return message(null, key, replacements);
    }

    public String message(UUID uuid, TranslationKey translationKey, Object... replacements) {
        return message(uuid, translationKey.getKey(), replacements);
    }

    public String message(UUID uuid, String key, Object... replacements) {
        if(FuturePlots.getSettings().isDefaultLangEnabled()) {
            this.locale = FuturePlots.getSettings().getDefaultLanguage();
        }
        return handler.message(uuid, this.locale, key, replacements);
    }
}
