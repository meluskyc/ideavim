@file:Suppress("RemoveCurlyBracesFromTemplate")

package org.jetbrains.plugins.ideavim.action.change.delete

import com.maddyhome.idea.vim.command.CommandState
import com.maddyhome.idea.vim.helper.StringHelper.parseKeys
import com.maddyhome.idea.vim.helper.VimBehaviourDiffers
import org.jetbrains.plugins.ideavim.VimTestCase

class DeleteVisualLinesEndActionTest : VimTestCase() {
    fun `test simple deletion`() {
        val keys = parseKeys("v", "D")
        val before = """
            A Discovery

            I ${c}found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
        """.trimIndent()
        val after = """
            A Discovery

            ${c}all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
        """.trimIndent()
        doTest(keys, before, after, CommandState.Mode.COMMAND, CommandState.SubMode.NONE)
    }

    @VimBehaviourDiffers(originalVimAfter = """
            A Discovery

                ${c}all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
    """)
    fun `test simple deletion with indent`() {
        val keys = parseKeys("v", "D")
        val before = """
            A Discovery

            I ${c}found it in a legendary land
                all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
        """.trimIndent()
        val after = """
            A Discovery

            ${c}    all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
        """.trimIndent()
        doTest(keys, before, after, CommandState.Mode.COMMAND, CommandState.SubMode.NONE)
    }

    fun `test simple deletion empty line`() {
        val keys = parseKeys("v", "D")
        val before = """
            A Discovery
            ${c}
            I found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
        """.trimIndent()
        val after = """
            A Discovery
            ${c}I found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
        """.trimIndent()
        doTest(keys, before, after, CommandState.Mode.COMMAND, CommandState.SubMode.NONE)
    }

    @VimBehaviourDiffers("""
            A Discovery

            I found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            ${c}
    """)
    fun `test simple deletion last line`() {
        val keys = parseKeys("v", "D")
        val before = """
            A Discovery

            I found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the ${c}torrent of a mountain pass.

        """.trimIndent()
        val after = """
            A Discovery

            I found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden san${c}d

        """.trimIndent()
        doTest(keys, before, after, CommandState.Mode.COMMAND, CommandState.SubMode.NONE)
    }

    fun `test simple deletion first line`() {
        val keys = parseKeys("v", "D")
        val before = """
            A ${c}Discovery

            I found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
        """.trimIndent()
        val after = """
            ${c}
            I found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
        """.trimIndent()
        doTest(keys, before, after, CommandState.Mode.COMMAND, CommandState.SubMode.NONE)
    }

    fun `test simple deletion before empty`() {
        val keys = parseKeys("v", "D")
        val before = """
            A Discovery

            I found it in a legendary land
            all ${c}rocks and lavender and tufted grass,

            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
        """.trimIndent()
        val after = """
            A Discovery

            I found it in a legendary land
            ${c}
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
        """.trimIndent()
        doTest(keys, before, after, CommandState.Mode.COMMAND, CommandState.SubMode.NONE)
    }

    @VimBehaviourDiffers("""
            A Discovery

            I found it in a legendary land
            all rocks and lavender and tufted grass,
            ${c}where it was settled on some sodden sand"""
    )
    fun `test simple deletion last line without empty line`() {
        val keys = parseKeys("v", "D")
        val before = """
            A Discovery

            I found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the ${c}torrent of a mountain pass.""".trimIndent()
        val after = """
            A Discovery

            I found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden san${c}d

            """.trimIndent()
        doTest(keys, before, after, CommandState.Mode.COMMAND, CommandState.SubMode.NONE)
    }

    fun `test simple deletion multiline`() {
        val keys = parseKeys("vj", "D")
        val before = """
            A Discovery

            I ${c}found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
            """.trimIndent()
        val after = """
            A Discovery

            ${c}where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
            """.trimIndent()
        doTest(keys, before, after, CommandState.Mode.COMMAND, CommandState.SubMode.NONE)
    }

    fun `test simple deletion multiline motion up`() {
        val keys = parseKeys("vk", "D")
        val before = """
            A Discovery

            I found it in a legendary land
            all ${c}rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
            """.trimIndent()
        val after = """
            A Discovery

            ${c}where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
            """.trimIndent()
        doTest(keys, before, after, CommandState.Mode.COMMAND, CommandState.SubMode.NONE)
    }

    fun `test delete visual lines end action`() {
        typeTextInFile(parseKeys("v", "2j", "D"),
                """
                    a${c}bcde
                    abcde
                    abcde
                    abcde
                    abcd${c}e
                    abcde
                    abcde

                    """.trimIndent())
        myFixture.checkResult("<caret>abcd<caret>e\n")
    }

    fun `test line simple deletion`() {
        val keys = parseKeys("V", "D")
        val before = """
            A Discovery

            I ${c}found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
        """.trimIndent()
        val after = """
            A Discovery

            ${c}all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
        """.trimIndent()
        doTest(keys, before, after, CommandState.Mode.COMMAND, CommandState.SubMode.NONE)
    }

    @VimBehaviourDiffers(originalVimAfter = """
            A Discovery

                ${c}all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
    """)
    fun `test line deletion with indent`() {
        val keys = parseKeys("V", "D")
        val before = """
            A Discovery

            I ${c}found it in a legendary land
                all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
        """.trimIndent()
        val after = """
            A Discovery

            ${c}    all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
        """.trimIndent()
        doTest(keys, before, after, CommandState.Mode.COMMAND, CommandState.SubMode.NONE)
    }

    fun `test line deletion empty line`() {
        val keys = parseKeys("V", "D")
        val before = """
            A Discovery
            ${c}
            I found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
        """.trimIndent()
        val after = """
            A Discovery
            ${c}I found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
        """.trimIndent()
        doTest(keys, before, after, CommandState.Mode.COMMAND, CommandState.SubMode.NONE)
    }

    @VimBehaviourDiffers("""
            A Discovery

            I found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            ${c}
    """)
    fun `test line deletion last line`() {
        val keys = parseKeys("V", "D")
        val before = """
            A Discovery

            I found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the ${c}torrent of a mountain pass.

        """.trimIndent()
        val after = """
            A Discovery

            I found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden san${c}d

        """.trimIndent()
        doTest(keys, before, after, CommandState.Mode.COMMAND, CommandState.SubMode.NONE)
    }

    @VimBehaviourDiffers("""
            A Discovery

            I found it in a legendary land
            all rocks and lavender and tufted grass,
            ${c}where it was settled on some sodden sand"""
    )
    fun `test line deletion last line without empty line`() {
        val keys = parseKeys("V", "D")
        val before = """
            A Discovery

            I found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the ${c}torrent of a mountain pass.""".trimIndent()
        val after = """
            A Discovery

            I found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand

            """.trimIndent()
        doTest(keys, before, after, CommandState.Mode.COMMAND, CommandState.SubMode.NONE)
    }

    fun `test line deletion multiline`() {
        val keys = parseKeys("Vj", "D")
        val before = """
            A Discovery

            I ${c}found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
            """.trimIndent()
        val after = """
            A Discovery

            ${c}where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
            """.trimIndent()
        doTest(keys, before, after, CommandState.Mode.COMMAND, CommandState.SubMode.NONE)
    }

    fun `test line deletion multiline motion up`() {
        val keys = parseKeys("Vk", "D")
        val before = """
            A Discovery

            I found it in a legendary land
            all ${c}rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
            """.trimIndent()
        val after = """
            A Discovery

            ${c}where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
            """.trimIndent()
        doTest(keys, before, after, CommandState.Mode.COMMAND, CommandState.SubMode.NONE)
    }

    fun `test line delete visual lines end action`() {
        typeTextInFile(parseKeys("V", "2j", "D"),
                """
                    a${c}bcde
                    abcde
                    abcde
                    abcde
                    abcd${c}e
                    abcde
                    abcde

                    """.trimIndent())
        myFixture.checkResult("<caret>abcd<caret>e\n")
    }

    fun `test block simple deletion`() {
        val keys = parseKeys("<C-V>", "D")
        val before = """
            A Discovery

            I${c} found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
        """.trimIndent()
        val after = """
            A Discovery

            I
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
        """.trimIndent()
        doTest(keys, before, after, CommandState.Mode.COMMAND, CommandState.SubMode.NONE)
    }

    fun `test block deletion empty line`() {
        val keys = parseKeys("<C-V>", "D")
        val before = """
            A Discovery
            ${c}
            I found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
        """.trimIndent()
        val after = """
            A Discovery
            ${c}
            I found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
        """.trimIndent()
        doTest(keys, before, after, CommandState.Mode.COMMAND, CommandState.SubMode.NONE)
    }

    fun `test block deletion last line`() {
        val keys = parseKeys("<C-V>", "D")
        val before = """
            A Discovery

            I found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the${c} torrent of a mountain pass.

        """.trimIndent()
        val after = """
            A Discovery

            I found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the

        """.trimIndent()
        doTest(keys, before, after, CommandState.Mode.COMMAND, CommandState.SubMode.NONE)
    }

    fun `test block deletion last line without empty line`() {
        val keys = parseKeys("<C-V>", "D")
        val before = """
            A Discovery

            I found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the${c} torrent of a mountain pass.""".trimIndent()
        val after = """
            A Discovery

            I found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the""".trimIndent()
        doTest(keys, before, after, CommandState.Mode.COMMAND, CommandState.SubMode.NONE)
    }

    fun `test block deletion multiline`() {
        val keys = parseKeys("<C-V>j", "D")
        val before = """
            A Discovery

            I${c} found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
            """.trimIndent()
        val after = """
            A Discovery

            I
            a
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
            """.trimIndent()
        doTest(keys, before, after, CommandState.Mode.COMMAND, CommandState.SubMode.NONE)
    }

    fun `test block deletion multiline motion up`() {
        val keys = parseKeys("<C-V>k", "D")
        val before = """
            A Discovery

            I found it in a legendary land
            all${c} rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
            """.trimIndent()
        val after = """
            A Discovery

            I f
            all
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
            """.trimIndent()
        doTest(keys, before, after, CommandState.Mode.COMMAND, CommandState.SubMode.NONE)
    }

    fun `test delete visual block line end action`() {
        typeTextInFile(parseKeys("<C-V>", "2j", "2l", "D"),
                """
                    abcde
                    a<caret>bcde
                    abcde
                    abcde
                    abcde

                    """.trimIndent())
        myFixture.checkResult(("""
    abcde
    <caret>a
    a
    a
    abcde

    """.trimIndent()))
    }
}