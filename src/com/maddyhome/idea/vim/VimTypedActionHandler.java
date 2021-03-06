/*
 * IdeaVim - Vim emulator for IDEs based on the IntelliJ platform
 * Copyright (C) 2003-2019 The IdeaVim authors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.maddyhome.idea.vim;

import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.ActionPlan;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.editor.actionSystem.TypedActionHandlerEx;
import com.maddyhome.idea.vim.helper.EditorDataContext;
import com.maddyhome.idea.vim.listener.SelectionVimListenerSuppressor;
import com.maddyhome.idea.vim.listener.VimListenerSuppressor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * Accepts all regular keystrokes and passes them on to the Vim key handler.
 *
 * IDE shortcut keys used by Vim commands are handled by {@link com.maddyhome.idea.vim.action.VimShortcutKeyAction}.
 */
public class VimTypedActionHandler implements TypedActionHandlerEx {
  private static final Logger logger = Logger.getInstance(VimTypedActionHandler.class.getName());

  @NotNull private final KeyHandler handler;

  public VimTypedActionHandler(TypedActionHandler origHandler) {
    handler = KeyHandler.getInstance();
    handler.setOriginalHandler(origHandler);
  }

  @Override
  public void beforeExecute(@NotNull Editor editor, char charTyped, @NotNull DataContext context, @NotNull ActionPlan plan) {
    if (isEnabled(editor)) {
      handler.beforeHandleKey(editor, KeyStroke.getKeyStroke(charTyped), context, plan);
    }
    else {
      TypedActionHandler originalHandler = handler.getOriginalHandler();
      if (originalHandler instanceof TypedActionHandlerEx) {
        ((TypedActionHandlerEx)originalHandler).beforeExecute(editor, charTyped, context, plan);
      }
    }
  }

  @Override
  public void execute(@NotNull final Editor editor, final char charTyped, @NotNull final DataContext context) {
    if (isEnabled(editor) && charTyped != KeyEvent.CHAR_UNDEFINED) {
      try {
        handler.handleKey(editor, KeyStroke.getKeyStroke(charTyped), new EditorDataContext(editor));
      }
      catch (Throwable e) {
        logger.error(e);
      }
    }
    else {
      try (final VimListenerSuppressor ignored = SelectionVimListenerSuppressor.INSTANCE.lock()) {
        TypedActionHandler origHandler = handler.getOriginalHandler();
        origHandler.execute(editor, charTyped, context);
      }
    }
  }

  private boolean isEnabled(@NotNull Editor editor) {
    if (VimPlugin.isEnabled()) {
      final Lookup lookup = LookupManager.getActiveLookup(editor);
      return lookup == null || !lookup.isFocused();
    }
    return false;
  }
}
