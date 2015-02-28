package com.jetbrains.lang.dart.ide.template;

import com.intellij.codeInsight.template.EverywhereContextType;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.util.Condition;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.ProcessingContext;
import com.jetbrains.lang.dart.DartBundle;
import com.jetbrains.lang.dart.DartLanguage;
import com.jetbrains.lang.dart.DartTokenTypesSets;
import com.jetbrains.lang.dart.highlight.DartSyntaxHighlighter;
import com.jetbrains.lang.dart.psi.*;
import com.jetbrains.lang.dart.psi.impl.DartSimpleFormalParameterImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLLanguage;
import org.jetbrains.yaml.psi.YAMLDocument;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public abstract class DartTemplateContextType extends TemplateContextType {

  protected DartTemplateContextType(@NotNull @NonNls String id,
                                    @NotNull String presentableName,
                                    @Nullable Class<? extends TemplateContextType> baseContextType) {
    super(id, presentableName, baseContextType);
  }

  @Override
  public boolean isInContext(@NotNull final PsiFile file, final int offset) {
    if (PsiUtilCore.getLanguageAtOffset(file, offset).isKindOf(DartLanguage.INSTANCE)
        || PsiUtilCore.getLanguageAtOffset(file,offset).isKindOf(YAMLLanguage.INSTANCE)) {
      PsiElement element = file.findElementAt(offset);

      if (element instanceof PsiWhiteSpace) {
        return false;
      }
      return element != null && isInContext(element);
    }

    return false;
  }

  protected abstract boolean isInContext(@NotNull PsiElement element);

  public static class Generic extends DartTemplateContextType {
    public Generic() {
      super("DART", "Dart", EverywhereContextType.class);
    }
    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
      return true;
    }
  }


  public static class Statement extends DartTemplateContextType {
    public Statement() {
      super("DART_STATEMENT", "Statement", Generic.class);
    }

    @Override
    protected boolean isInContext(@NotNull PsiElement element) {

      PsiElement goodParent = PsiTreeUtil.getParentOfType(element,
                                                          DartStatements.class);
      PsiElement badParent = PsiTreeUtil.getParentOfType(element,
                                                         DartVarInit.class);

      if (badParent != null) {
        return false;
      } else if (goodParent != null) {
        PsiElement searchParent = element;
        while (!(searchParent.getParent() instanceof DartStatements)) {
          searchParent = searchParent.getParent();
        }
        if (searchParent.getText().startsWith(element.getText())) {
          return true;
        }
      } else {
        return false;
      }
      return false;
    }
  }

  public static class TopLevel extends DartTemplateContextType {
    public TopLevel() {
      super("DART_TOP_LEVEL", "Top-Level", Generic.class);
    }

    @Override
    protected boolean isInContext(@NotNull PsiElement element) {

      PsiElement goodParent = PsiTreeUtil.getParentOfType(element,
                                                          DartFile.class);
      PsiElement badParent = PsiTreeUtil.getParentOfType(element,
                                                         DartStatements.class,
                                                         DartClassMembers.class);
      if (badParent != null) {
        return false;
      } else if (goodParent != null) {
        PsiElement searchParent = element;
        while (!(searchParent.getParent() instanceof DartFile)) {
          searchParent = searchParent.getParent();
        }
        if (searchParent.getText().startsWith(element.getText()) && element.getPrevSibling() == null) {
          return true;
        }
        return false;
      } else {
        return false;
      }
    }
  }

  public static class ClassTopLevel extends DartTemplateContextType {
    public ClassTopLevel() {
      super("DART_CLASS_TOP_LEVEL", "Class Top-Level", Generic.class);
    }

    @Override
    protected boolean isInContext(@NotNull PsiElement element) {

      PsiElement goodParent = PsiTreeUtil.getParentOfType(element,
                                                          DartClassMembers.class);
      PsiElement badParent = PsiTreeUtil.getParentOfType(element,
                                                         DartStatements.class);
      if (badParent != null) {
        return false;
      } else if (goodParent != null) {
        PsiElement searchParent = element;
        while (!(searchParent.getParent() instanceof DartClassMembers)) {
          searchParent = searchParent.getParent();
        }
        if (searchParent.getText().startsWith(element.getText())) {
          return true;
        }
        return false;
      } else {
        return false;
      }
    }
  }

  //public static class Expression extends DartTemplateContextType {
  //
  //  public Expression() {
  //    super("DART_EXPRESSION", "Expression", Generic.class);
  //  }
  //
  //  @Override
  //  protected boolean isInContext(@NotNull PsiElement element) {
  //
  //    PsiElement goodParent = PsiTreeUtil.getParentOfType(element,
  //                                                        DartExpression.class);
  //    PsiElement badParent = PsiTreeUtil.getParentOfType(element,
  //                                                       DartStatements.class);
  //    if (badParent != null) {
  //      return false;
  //    } else if (goodParent != null) {
  //      return true;
  //    } else {
  //      return false;
  //    }
  //  }
  //}
  //
  //public static class Declaration extends DartTemplateContextType {
  //  public Declaration() {
  //    super("DART_DECLARATION", "Declaration", Generic.class);
  //  }
  //  @Override
  //  protected boolean isInContext(@NotNull PsiElement element) {
  //    PsiElement goodParent = PsiTreeUtil.getParentOfType(element,
  //                                                        DartVarAccessDeclaration.class);
  //    if (goodParent != null) {
  //      return true;
  //    } else {
  //      return false;
  //    }
  //  }
  //}
  public static class Html extends DartTemplateContextType {
    public Html() {
      super("DART_HTML", "HTML Include", Generic.class);
    }
    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
      if (element.getContainingFile().getLanguage().isKindOf(HTMLLanguage.INSTANCE)) {
        return true;
      }
      return false;
    }
  }

  public static class Yaml extends DartTemplateContextType {
    public Yaml() {
      super("DART_YAML", "YAML", Generic.class);
    }
    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
      if (element.getContainingFile().getLanguage().isKindOf(YAMLLanguage.INSTANCE) && element.getContainingFile().getName().equals("pubspec.yaml")) {
        return true;
      }
      return false;
    }
  }
}


