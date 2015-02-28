package com.jetbrains.lang.dart.ide.template.macro;

import com.intellij.codeInsight.template.*;
import com.jetbrains.lang.dart.ide.template.DartTemplateContextType;
import org.jetbrains.annotations.NotNull;

/**
 * @author: Fedor.Korotkov
 */
/*
 * Copyright 2000-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class DartSuggestForStep extends Macro {
  @Override
  public String getName() {
    return "dartSuggestForStep";
  }

  @Override
  public String getPresentableName() {
    return "dartSuggestForStep(START,END)";
  }

  @Override
  @NotNull
  public String getDefaultValue() {
    return "++";
  }

  @Override
  public Result calculateResult(@NotNull Expression[] params, ExpressionContext context) {

    String start = params[0].calculateResult(context).toString();
    String end = params[1].calculateResult(context).toString();
    int parseStart, parseEnd;
    try {
      parseStart = Integer.parseInt(start);
    }
    catch (NumberFormatException e) {
      parseStart = 100000;
    }
    try {
      parseEnd = Integer.parseInt(end);
    }
    catch (NumberFormatException e) {
      parseEnd = 100001;
    }

    if (parseEnd > parseStart) {
      return new TextResult("++");
    }
    else {
      return new TextResult("--");
    }
  }


  @Override
  public final boolean isAcceptableInContext(final TemplateContextType context) {
    return context instanceof DartTemplateContextType;
  }
}

