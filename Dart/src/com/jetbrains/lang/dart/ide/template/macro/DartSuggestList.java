package com.jetbrains.lang.dart.ide.template.macro;

import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.codeInsight.template.*;
import com.intellij.codeInsight.template.macro.MacroUtil;
import com.intellij.ide.IconProvider;
import com.intellij.openapi.graph.option.IconOptionItem;
import com.intellij.psi.*;
import com.intellij.util.ArrayUtil;
import com.jetbrains.lang.dart.DartIconProvider;
import com.jetbrains.lang.dart.ide.template.DartTemplateContextType;
import com.jetbrains.lang.dart.util.DartRefactoringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Set;

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

import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.PsiTypeLookupItem;
import com.intellij.codeInsight.template.*;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DartSuggestList extends Macro {
  @Override
  public String getName() {
    return "suggestList";
  }

  @Override
  public String getPresentableName() {
    return "suggestList()";
  }

  @Override
  @NotNull
  public String getDefaultValue() {
    return "a";
  }

  @Override
  public Result calculateResult(@NotNull Expression[] params, ExpressionContext context) {

    return new TextResult(params[0].calculateResult(context).toString());
  }

  @Override
  public LookupElement[] calculateLookupItems(@NotNull Expression[] params, final ExpressionContext context) {
    LookupItem[] items = new LookupItem[params.length];
    for (int i = 0; i < params.length; i++) {
      Expression param = params[i];
      items[i] = LookupItem.fromString(param.calculateResult(context).toString());
      items[i].setIcon(context.getPsiElementAtStartOffset().getContainingFile().getIcon(0));
    }
    return items;
  }


  @Override
  public final boolean isAcceptableInContext(final TemplateContextType context) {
    return context instanceof DartTemplateContextType;
  }
}

