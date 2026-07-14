# Breakout Supabase setup

Use this for real online leagues, live drafts, shared rosters, invite codes, and cross-device sync.

Supabase is the right free backend for this version because the app already uses it for auth and league data. Keep all server-owned draft logic in Supabase so every phone reads the same draft state.

## 1. Create `secrets.properties`

In the project root, create:

```properties
SUPABASE_URL=replace_me
SUPABASE_ANON_KEY=replace_me
```

Use `secrets.properties`, not Android Studio's generated `local.properties`.

Do not put the service-role key or database password in the Android app.

## 2. Create the Supabase project

1. Go to https://supabase.com/dashboard.
2. Create a new project on the Free plan.
3. Open the project.
4. Go to **SQL Editor**.
5. Open `supabase/migrations/0001_breakout_core.sql`.
6. Paste the whole file into the SQL Editor.
7. Run it.

This creates the shared online tables:

- profiles
- leagues
- league_members
- artists
- rosters
- draft_picks
- notifications

It also enables Row Level Security and adds server-side functions for accounts, invite-code joins, league settings, moderation, and draft state.

If Supabase says a function already exists with a different return type, run the newest full SQL file again. The migration now drops and recreates functions that need a changed return type.

## 3. Get the Supabase app values

In the Supabase dashboard:

1. Open your project.
2. Open **Connect** or **Settings > API Keys**.
3. Copy the project URL into `SUPABASE_URL`.
4. Copy the publishable key, or legacy anon key, into `SUPABASE_ANON_KEY`.

Do not put the secret key or service-role key in the Android app.

## 4. Enable auth settings

In Supabase:

1. Go to **Authentication**.
2. Keep email auth enabled.
3. Use email/password for the first version.
4. Set minimum password length to 8 if you want the app and Supabase rules to match.
5. If you do not want users blocked waiting for verification during testing, disable required email confirmations.

For Google sign-in later, enable Google in **Authentication > Providers**, then add the Android OAuth client information from Google Cloud.

## 5. Realtime

Realtime is what makes drafts update on every phone without manually refreshing.

In Supabase:

1. Go to **Database > Replication** or **Realtime**.
2. Enable realtime for these tables:
   - leagues
   - league_members
   - draft_picks
   - rosters
3. Keep Row Level Security enabled.

## 6. Draft server authority

The phone should not decide draft order, timers, or whether a pick is legal. Supabase should.

The app should call one server function when a user picks:

```text
make_draft_pick(league_id, artist_id, roster_slot, expected_pick_index)
```

That function should:

- check the user is on the clock
- reject already drafted artists
- reject invalid roster slots
- insert the draft pick
- update the user's roster
- advance the league's current pick
- set the next pick start time from the server clock

Every phone then watches the same `leagues`, `draft_picks`, and `rosters` rows.

## 7. Artist data

The current Android prototype uses a no-key artist feed for search, audience scale, and artist images.

For production, Spotify is a good candidate for artist images, followers, popularity, and IDs. Spotify client secrets should stay in a backend function, not in the Android app.
