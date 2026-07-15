# Breakout UI Layout Guide

This app uses Jetpack Compose. Android Studio can preview Compose screens, but it does not offer the same drag-and-drop visual editor that XML layouts use.

## Where Screens Live

- `MainActivity.kt`: app startup, app-wide state, services, and data models.
- `ui/screens/SetupScreens.kt`: sign in, password reset, and create/join league screens.
- `ui/screens/HomeScreen.kt`: league home dashboard and draft countdown.
- `ui/screens/MarketScreen.kt`: market search, filters, artist cards, and market loading behavior.
- `ui/screens/RosterScreen.kt`: roster slots, swaps, waivers, and waiver order.
- `ui/screens/DraftScreens.kt`: draft setup, lobby, room, pick strip, and draft summary.
- `ui/screens/MatchupScreens.kt`: matchup, all matchups, schedule, and head-to-head rows.
- `ui/screens/LeagueScreens.kt`: standings and league settings.
- `ui/screens/AccountScreen.kt`: account settings and member review dialogs.
- `ui/screens/ArtistAndComponents.kt`: artist detail page plus shared UI components used across screens.

## Where To Edit Spacing

Most common sizes live in `core/designsystem/Spacing.kt`.

- `ScreenHorizontalPadding`: left/right page margin.
- `SectionSpacing`: space near the bottom of page content.
- `CardSpacing`: vertical gap between cards.
- `CardPadding`: padding inside normal cards.
- `HeroCardPadding`: padding inside larger feature cards.
- `ArtworkList` and `ArtworkCard`: artist image sizes in rows/cards.
- `MinimumTouchTarget`: standard button/tap target size.

If one screen needs a very specific tweak, edit that screen file directly. If the same spacing issue shows up everywhere, edit `Spacing.kt`.
