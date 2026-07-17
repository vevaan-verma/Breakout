drop function if exists public.swap_my_roster_slots(uuid, text, text);

create or replace function public.swap_my_roster_slots(
    target_league_id uuid,
    from_slot text,
    to_slot text
)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
    current_user_id uuid := auth.uid();
    from_artist uuid;
    to_artist uuid;
begin
    if current_user_id is null then
        raise exception 'Sign in again to move artists.';
    end if;

    if target_league_id is null or trim(coalesce(from_slot, '')) = '' or trim(coalesce(to_slot, '')) = '' then
        raise exception 'Choose two roster slots to swap.';
    end if;

    if from_slot = to_slot then
        return;
    end if;

    if not exists (
        select 1
        from public.league_members
        where league_id = target_league_id
        and user_id = current_user_id
        and status = 'active'
    ) then
        raise exception 'You are not in this league.';
    end if;

    select artist_id into from_artist
    from public.rosters
    where league_id = target_league_id
    and user_id = current_user_id
    and roster_slot = from_slot
    and released_at is null
    limit 1;

    if from_artist is null then
        raise exception 'Source slot is empty.';
    end if;

    select artist_id into to_artist
    from public.rosters
    where league_id = target_league_id
    and user_id = current_user_id
    and roster_slot = to_slot
    and released_at is null
    limit 1;

    if not public.artist_fits_roster_slot(from_artist, to_slot) then
        raise exception 'Artist does not fit the target slot.';
    end if;

    if to_artist is not null and not public.artist_fits_roster_slot(to_artist, from_slot) then
        raise exception 'Target artist does not fit the source slot.';
    end if;

    if to_artist is null then
        update public.rosters
        set roster_slot = to_slot
        where league_id = target_league_id
        and user_id = current_user_id
        and artist_id = from_artist
        and released_at is null;

        update public.draft_picks
        set roster_slot = to_slot
        where league_id = target_league_id
        and user_id = current_user_id
        and artist_id = from_artist;
    else
        update public.rosters
        set roster_slot = case
            when artist_id = from_artist then to_slot
            when artist_id = to_artist then from_slot
            else roster_slot
        end
        where league_id = target_league_id
        and user_id = current_user_id
        and released_at is null
        and artist_id in (from_artist, to_artist);

        update public.draft_picks
        set roster_slot = case
            when artist_id = from_artist then to_slot
            when artist_id = to_artist then from_slot
            else roster_slot
        end
        where league_id = target_league_id
        and user_id = current_user_id
        and artist_id in (from_artist, to_artist);
    end if;
end;
$$;

grant execute on function public.swap_my_roster_slots(uuid, text, text) to authenticated;
