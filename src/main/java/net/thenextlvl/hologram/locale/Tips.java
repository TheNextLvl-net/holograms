package net.thenextlvl.hologram.locale;

import java.util.List;

public final class Tips {
    public static final String PREFIX = "hologram.tip.";
    public static final List<String> TIPS_ENGLISH = List.of("""
                    <hologram>
                    Use <gray>/hologram line add <hologram> \\<type> [\\<value>]</gray>
                    to add a new line
                    Use <gray>/hologram delete <hologram></gray>
                    to remove the hologram""",
            """
                    <hologram>
                    Use <gray>/hologram line remove <hologram> \\<line></gray>
                    to remove a line
                    Use <gray>/hologram rename <hologram> \\<name></gray>
                    to rename the hologram""",
            """
                    <hologram>
                    Use <gray>/hologram teleport <hologram> <player></gray>
                    to move the hologram to your location
                    Use <gray>/hologram view-permission <hologram> \\<permission></gray>
                    to set a view permission on the hologram""",
            """
                    <hologram>
                    Use <gray>/hologram line swap <hologram> \\<first> \\<second></gray>
                    to swap the order of two lines
                    Use <gray>/hologram line move <hologram> \\<from> \\<to></gray>
                    to move a line in the hologram""",
            """
                    <hologram>
                    Use <gray>/hologram line insert <hologram> \\<line> \\<type> [\\<value>]</gray>
                    to insert a line above another line
                    Use <gray>/hologram line edit <hologram> \\<line> …</gray>
                    to edit a line in a hologram""",
            """
                    <hologram>
                    Use <gray>/hologram page add <hologram> \\<line> \\<type> [\\<value>]</gray>
                    to add a page to a line
                    Use <gray>/hologram page remove <hologram> \\<line> \\<page></gray>
                    to remove a page from a line""",
            """
                    <hologram>
                    Use <gray>/hologram page swap <hologram> \\<line> \\<first> \\<second></gray>
                    to swap the order of two pages
                    Use <gray>/hologram page move <hologram> \\<line> \\<from> \\<to></gray>
                    to move a page in a line""",
            """
                    <hologram>
                    Use <gray>/hologram page insert <hologram> \\<line> \\<page> \\<type> [\\<value>]</gray>
                    to insert a page before another page
                    Use <gray>/hologram page edit <hologram> \\<line> \\<page> …</gray>
                    to edit a page in a line""",
            """
                    <hologram>
                    Use <gray>/hologram page clear <hologram> \\<line></gray>
                    to clear all pages from a line""",
            """
                    <hologram>
                    You can use <gray>PlaceholderAPI</gray>
                    and <gray>MiniPlaceholders</gray> tags
                    in your holograms""",
            """
                    <hologram>
                    To embed images in your holograms
                    use the <gray><image:…></gray> tag
                    Supports <gray>URLs</gray> and <gray>File paths</gray>""");
    public static final List<String> TIPS_GERMAN = List.of("""
                    <hologram>
                    Verwende <gray>/hologram line add <hologram> \\<type> [\\<value>]</gray>
                    um eine neue Zeile hinzuzufügen
                    Verwende <gray>/hologram delete <hologram></gray>
                    um das Hologramm zu entfernen""",
            """
                    <hologram>
                    Verwende <gray>/hologram line remove <hologram> \\<line></gray>
                    um eine Zeile zu entfernen
                    Verwende <gray>/hologram rename <hologram> \\<name></gray>
                    um das Hologramm umzubenennen""",
            """
                    <hologram>
                    Verwende <gray>/hologram teleport <hologram> <player></gray>
                    um das Hologramm an deiner Position zu platzieren
                    Verwende <gray>/hologram view-permission <hologram> \\<permission></gray>
                    um eine Sehberechtigung auf das Hologramm zu setzen""",
            """
                    <hologram>
                    Verwende <gray>/hologram line swap <hologram> \\<first> \\<second></gray>
                    um die Reihenfolge zweier Zeilen zu tauschen
                    Verwende <gray>/hologram line move <hologram> \\<from> \\<to></gray>
                    um eine Zeile im Hologramm zu verschieben""",
            """
                    <hologram>
                    Verwende <gray>/hologram line insert <hologram> \\<line> \\<type> [\\<value>]</gray>
                    um eine Zeile oberhalb einer anderen Zeile einzufügen
                    Verwende <gray>/hologram line edit <hologram> \\<line> …</gray>
                    um eine Zeile im Hologramm zu bearbeiten""",
            """
                    <hologram>
                    Verwende <gray>/hologram page add <hologram> \\<line> \\<type> [\\<value>]</gray>
                    um eine Seite zu einer paginierten Zeile hinzuzufügen
                    Verwende <gray>/hologram page remove <hologram> \\<line> \\<page></gray>
                    um eine Seite aus einer Zeile zu entfernen""",
            """
                    <hologram>
                    Verwende <gray>/hologram page swap <hologram> \\<line> \\<first> \\<second></gray>
                    um die Reihenfolge zweier Seiten zu tauschen
                    Verwende <gray>/hologram page move <hologram> \\<line> \\<from> \\<to></gray>
                    um eine Seite in einer Zeile zu verschieben""",
            """
                    <hologram>
                    Verwende <gray>/hologram page insert <hologram> \\<line> \\<page> \\<type> [\\<value>]</gray>
                    um eine Seite vor einer anderen Seite einzufügen
                    Verwende <gray>/hologram page edit <hologram> \\<line> \\<page> …</gray>
                    um eine Seite in einer Zeile zu bearbeiten""",
            """
                    <hologram>
                    Verwende <gray>/hologram page clear <hologram> \\<line></gray>
                    um alle Seiten aus einer Zeile zu entfernen""",
            """
                    <hologram>
                    Du kannst <gray>PlaceholderAPI</gray>
                    und <gray>MiniPlaceholders</gray> Tags
                    in deinen Hologrammen verwenden""",
            """
                    <hologram>
                    Um Bilder in deinen Hologrammen einzubetten
                    verwende den <gray><image:…></gray> Tag
                    Unterstützt <gray>URLs</gray> und <gray>Dateipfade</gray>""");
}
