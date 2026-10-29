# Compose Effect Lab: screen design

A working Android learning tool, not a marketing page. Outfit handles labels, instructions and titles; platform monospace is reserved for code and raw trace messages. Local font files and the complete OFL notice are embedded. No runtime font provider, network analytics or decorative animation is introduced.

## Material and hierarchy

Paper #FAF8F3, ink #202421, muted #5E625C and a single copper accent #9B432F. Controls use a 12 dp radius explicitly; Material3 button defaults would otherwise render capsules. Gutters 20 dp, section gaps 20 dp and touch actions at least 48 dp. Text/selection keep native semantics; an underline and selected state identify the current tab, not color alone. Text contrasts: ink/paper 14.81:1, muted/paper 5.86:1, copper/paper 6.11:1, white/copper 6.49:1, copper/soft 5.18:1.

Open sections replace coloured cards. Each experiment presents its question, short code, controls and a flat timeline with sequence, source, kind and exact recorded message. No invented timestamps or execution metrics. Explanatory headings expose heading semantics; trace rows merge their text into a readable accessibility group. Code wraps at available width and the primary example uses explicit line breaks. Large text remains scrollable; it is not scaled down or truncated to fit.

## Ownership is part of the experience

An active experiment belongs to its screen composition, outside lazy list cells. Scrolling does not mean withdrawing the experiment. Changing its key or leaving its screen still cancels/releases the old owner. A regression journey found two starts of key 1 at 150% text when the original lazy-cell design was scrolled. It passes after moving ownership.

Latest-value delivery has its own tab. It can update a message during the wait without restarting, and the action becomes available again after delivery. Listener and real lifecycle examples retain acquisition/disposal behavior. The optional event observer reports the same snapshots rendered by the app and does not own work or resources.

Product strings use Spanish resources with an English fallback. Technical API names and probe messages such as `start:1` remain stable. The screen follows platform touch/ripple behavior without added motion. Native tests distinguish the host composable from the real MainActivity entry point; actual app captures are credited as emulator output, never physical-device evidence.

## Verification scope

Four JVM tests and nine instrumented tests on API 37: four probe contracts, four whole-screen journeys and one real MainActivity journey. The latter uses per-app locale support on API 33+; its English/Spanish captures run at 100% text. The whole suite also runs at 150% text,360×800dp. Lint keeps its target-version warning; debug native/ZIP alignment is audited but no R8 rate, optimized release, physical device or store readiness is established. CI runs JVM tests/lint/debug build; local device checks are separate.
