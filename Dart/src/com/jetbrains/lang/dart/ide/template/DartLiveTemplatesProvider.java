package com.jetbrains.lang.dart.ide.template;

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider;
import org.jetbrains.annotations.NonNls;

/**
 * @author: Fedor.Korotkov
 */
public class DartLiveTemplatesProvider implements DefaultLiveTemplatesProvider {
  private static final @NonNls String[] DEFAULT_TEMPLATES = new String[]{
    "/liveTemplates/dart_miscellaneous",
    "/liveTemplates/dart_iterations",
    "/liveTemplates/dart_surround",
    "/liveTemplates/dart_html",
    "/liveTemplates/dart_statements",
    "/liveTemplates/dart_import",
    "/liveTemplates/dart_yaml"
  };

  public String[] getDefaultLiveTemplateFiles() {
    return DEFAULT_TEMPLATES;
  }

  @Override
  public String[] getHiddenLiveTemplateFiles() {
    return null;
  }
}
