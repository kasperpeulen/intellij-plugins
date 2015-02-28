package com.jetbrains.lang.dart.ide.actions;

/*
 * CoDartright 2000-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a coDart of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.dart.server.generated.types.SourceEdit;
import com.intellij.codeInsight.editorActions.smartEnter.SmartEnterProcessor;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.lang.dart.DartTokenTypes;
import com.jetbrains.lang.dart.analyzer.DartAnalysisServerService;
import com.jetbrains.lang.dart.ide.surroundWith.statement.DartWithIfElseSurrounder;
import com.jetbrains.lang.dart.psi.*;
import com.jetbrains.lang.dart.psi.impl.DartFunctionDeclarationWithBodyOrNativeImpl;
import com.jetbrains.lang.dart.psi.impl.DartStatementsImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class DartSmartEnterProcessor extends SmartEnterProcessor {

  public boolean noEnter = false;

  @Override
  public boolean process(@NotNull final Project project, @NotNull final Editor editor, @NotNull final PsiFile psiFile) {

    PsiElement statementAtCaret = getStatementAtCaret(editor, psiFile);

    boolean noReformat = false;
    CaretModel caretModel = editor.getCaretModel();

    String string = editor.getDocument().getText().substring(caretModel.getVisualLineStart(), caretModel.getVisualLineEnd());

    if (statementAtCaret != null) {

      DartType type = PsiTreeUtil.findChildOfType(statementAtCaret, DartType.class);
      PsiElement atCaret = super.getStatementAtCaret(editor, psiFile);

      if (type != null && string.trim().endsWith("=") && string.trim().startsWith(type.getText())) {
        EditorModificationUtil.insertStringAtCaret(editor, " new " + type.getText() + "();", true, 0);
        EditorModificationUtil.moveCaretRelatively(editor, ((" new " + type.getText() + "(").length()));
        noEnter = true;
        noReformat = true;
        super.reformat(atCaret);
        string.substring(0, 1).toUpperCase();
      }
      else if (editor.getDocument().getText().charAt(caretModel.getOffset()) == ';') {

        realEnter(editor);
        EditorModificationUtil.insertStringAtCaret(editor, "  ..", true, 0);
        EditorModificationUtil.moveCaretRelatively(editor, 4);
        noReformat = true;
        super.reformat(statementAtCaret);
        noEnter = true;
      }
      else if (editor.getDocument().getText().charAt(caretModel.getOffset()) == ')' && editor.getDocument().getText().charAt(caretModel.getOffset()+1) == ';'
               && statementAtCaret.getText().contains("..")) {
        realEnter(editor);
        EditorModificationUtil.insertStringAtCaret(editor, "    ..", true, 0);
        EditorModificationUtil.moveCaretRelatively(editor, 6);
        super.reformat(atCaret);
        noReformat = true;
        noEnter = true;
      }

      else if (statementAtCaret instanceof DartSwitchStatement) {
        if (string.trim().startsWith("case") ||
            string.trim().startsWith("default")) {
          if (!string.trim().endsWith(":")) {
            editor.getDocument().insertString(caretModel.getVisualLineEnd() - 1, ":");
          }
        }
        else if (statementAtCaret.getText().endsWith("}") && statementAtCaret.getText().contains("{")) {
          plainBackSpace(editor);
          plainDown(editor);
        }
        else {
          caretModel.moveToOffset(statementAtCaret.getText().indexOf(")"));
          editor.getDocument().insertString(statementAtCaret.getTextRange().getEndOffset(), "{");
        }
      }
      else if (statementAtCaret instanceof DartIfStatement ||
               statementAtCaret instanceof DartWhileStatement ||
               statementAtCaret instanceof DartForStatement ||
               statementAtCaret instanceof DartFunctionDeclarationWithBodyOrNativeImpl ||
               statementAtCaret instanceof DartMethodDeclaration ||
               statementAtCaret instanceof DartWithIfElseSurrounder ||
               statementAtCaret instanceof DartDoWhileStatement ||
               statementAtCaret instanceof DartClassDefinition ||
               statementAtCaret instanceof DartTryStatement ||
               (statementAtCaret instanceof LeafElement && statementAtCaret.getText().equals("else") ||
                (type != null && type.getText().contains("Exception")))
        ) {
        if (statementAtCaret.getText().endsWith("}") || statementAtCaret.getText().endsWith(";")) {
          if (statementAtCaret.getLastChild().getText().length() -
              statementAtCaret.getLastChild().getText().replaceAll("\n", "").length() > 2) {

            int length = editor.getDocument().getTextLength();
            plainBackSpace(editor);
            int correctForBackspace = length - editor.getDocument().getTextLength();
            caretModel.moveToOffset(statementAtCaret.getTextRange().getEndOffset() - correctForBackspace);
            //plainDown(editor)

          } else  if (string.endsWith("{\n")) {
            action(editor, IdeActions.ACTION_EDITOR_MOVE_CARET_DOWN);
            noEnter = true;
          }
          else {
            action(editor, IdeActions.ACTION_EDITOR_DELETE_LINE);
            action(editor, IdeActions.ACTION_EDITOR_DELETE_TO_WORD_START);
            action(editor, IdeActions.ACTION_EDITOR_DELETE_TO_WORD_START);
            action(editor, IdeActions.ACTION_EDITOR_DELETE_TO_WORD_START);
            EditorModificationUtil.moveCaretRelatively(editor,-1);
            EditorModificationUtil.insertStringAtCaret(editor, ";", true, 1);
            action(editor, IdeActions.ACTION_EDITOR_ENTER);
            noReformat = true;
            noEnter = true;
          }
        }
        else {
          caretModel.moveToOffset(statementAtCaret.getTextRange().getEndOffset());
          editor.getDocument().insertString(statementAtCaret.getTextRange().getEndOffset(), "{");
        }
      }
      else {
        String nextSibling = statementAtCaret.getNextSibling() == null ? "" : statementAtCaret.getNextSibling().getText();
        final boolean statementEndsWithSemiColon = nextSibling.equals(DartTokenTypes.SEMICOLON.toString()) ||
                                                   statementAtCaret.getText().endsWith(DartTokenTypes.SEMICOLON.toString());
        if (!statementEndsWithSemiColon) {
          editor.getDocument().insertString(statementAtCaret.getTextRange().getEndOffset(), DartTokenTypes.SEMICOLON.toString());
        }
      }
    }
    if (!((statementAtCaret instanceof DartIfStatement || statementAtCaret instanceof DartTryStatement) &&
          statementAtCaret.getText().endsWith("}"))) {
      if (!noReformat) {
        super.reformat(statementAtCaret);
        noEnter = reformat(editor,psiFile);
      }
      if (!noEnter) plainEnter(editor);

    }
    else if (statementAtCaret.getText().contains("else {")) {
      if (!noReformat) {
        DartAnalysisServerService.getInstance().updateFilesContent();
        noEnter = reformat(editor,psiFile);
      }
      if (!noEnter) plainEnter(editor);

    }
    else if (!string.endsWith("{\n")){
      editor.getDocument().insertString(editor.getCaretModel().getOffset(), " ");
      editor.getCaretModel().moveToOffset(editor.getCaretModel().getOffset() + 1);
    }

    return true;
  }

  @Override
  public boolean processAfterCompletion(@NotNull Editor editor, @NotNull PsiFile psiFile) {
    return process(psiFile.getProject(), editor, psiFile);
  }

  @Override
  @Nullable
  protected PsiElement getStatementAtCaret(Editor editor, PsiFile psiFile) {

    PsiElement statementAtCaret = super.getStatementAtCaret(editor, psiFile);
    if (statementAtCaret != null) {
      while (!(statementAtCaret.getParent() instanceof DartStatements ||
               statementAtCaret.getParent() instanceof DartFile ||
               statementAtCaret.getParent() instanceof DartClassMembers)) {
        statementAtCaret = statementAtCaret.getParent();
      }
    }
    return statementAtCaret instanceof PsiWhiteSpace
           ? null : statementAtCaret;
  }

  private static EditorActionHandler getEnterHandler() {
    return EditorActionManager.getInstance().getActionHandler(IdeActions.ACTION_EDITOR_START_NEW_LINE);
  }

  private static EditorActionHandler getRealEnterHandler() {
    return EditorActionManager.getInstance().getActionHandler(IdeActions.ACTION_EDITOR_ENTER);
  }

  private static EditorActionHandler getBackSpaceHandler() {
    return EditorActionManager.getInstance().getActionHandler(IdeActions.ACTION_EDITOR_BACKSPACE);
  }

  private static EditorActionHandler getDownHandler() {
    return EditorActionManager.getInstance().getActionHandler(IdeActions.ACTION_EDITOR_MOVE_CARET_DOWN);
  }
  private static void action(@NotNull final Editor editor, @NotNull final String action) {
    EditorActionHandler handler = EditorActionManager.getInstance().getActionHandler(action);
    handler.execute(editor, ((EditorEx)editor).getDataContext());
  }


  public static void plainEnter(@NotNull final Editor editor) {
    getEnterHandler().execute(editor, ((EditorEx)editor).getDataContext());
  }

  public static void realEnter(@NotNull final Editor editor) {
    getRealEnterHandler().execute(editor, ((EditorEx)editor).getDataContext());
  }

  public static void plainBackSpace(@NotNull final Editor editor) {
    getBackSpaceHandler().execute(editor, ((EditorEx)editor).getDataContext());
  }

  public static void plainDown(@NotNull final Editor editor) {
    getDownHandler().execute(editor, ((EditorEx)editor).getDataContext());
  }

  public static boolean reformat(@NotNull final Editor editor, @NotNull final PsiFile file) {
    int start =  editor.getDocument().getTextLength();
    int begin = editor.getCaretModel().getOffset();
    DartAnalysisServerService.getInstance().updateFilesContent();
    DartAnalysisServerService.FormatResult formatResult =
      DartAnalysisServerService.getInstance().edit_format(file.getVirtualFile().getPath(), 0, 1 );
    if (formatResult != null ) {
      if (!formatResult.getEdits().isEmpty() && !formatResult.getEdits().get(0).getReplacement().equals(editor.getDocument().getText())) {
        for (SourceEdit edit : formatResult.getEdits()) {
          editor.getDocument().replaceString(0, editor.getDocument().getTextLength(), edit.getReplacement());
          int end = editor.getDocument().getTextLength();
          editor.getCaretModel().moveToOffset(begin + end - start);

        }
        return true;
      }
    }
    return false;
  }

}

