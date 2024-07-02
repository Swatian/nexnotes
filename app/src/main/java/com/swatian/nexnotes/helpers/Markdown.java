package com.swatian.nexnotes.helpers;

import android.content.Context;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import androidx.annotation.NonNull;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.SoftBreakAddsNewLinePlugin;
import io.noties.markwon.core.CorePlugin;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.ext.tables.TableAwareMovementMethod;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;
import io.noties.markwon.movement.MovementMethodPlugin;
import org.commonmark.node.Node;

/**
 * @author mmarif
 */
public class Markdown {

	public static void render(Context context, String text, TextView textView) {

		Markwon markwon =
				Markwon.builder(context)
						.usePlugin(CorePlugin.create())
						.usePlugin(HtmlPlugin.create())
						.usePlugin(LinkifyPlugin.create(true))
						.usePlugin(SoftBreakAddsNewLinePlugin.create())
						.usePlugin(TablePlugin.create(context))
						.usePlugin(
								MovementMethodPlugin.create(ScrollingMovementMethod.getInstance()))
						.usePlugin(MovementMethodPlugin.create(TableAwareMovementMethod.create()))
						.usePlugin(TaskListPlugin.create(context))
						.usePlugin(StrikethroughPlugin.create())
						.usePlugin(ImagesPlugin.create())
						.usePlugin(
								new AbstractMarkwonPlugin() {
									@Override
									public void configure(@NonNull Registry registry) {
										registry.require(ImagesPlugin.class);
									}
								})
						.build();

		Node node = markwon.parse(String.valueOf(text));
		Spanned markdown = markwon.render(node);
		markwon.setParsedMarkdown(textView, markdown);
	}
}
