update public.market_artist_cache
set
  snapshot_previous_listeners = coalesce(
    snapshot_previous_listeners,
    case
      when weekly_listener_growth_percent is not null
        and snapshot_listeners is not null
        and weekly_listener_growth_percent > -99.9
      then round(snapshot_listeners::numeric / (1 + (weekly_listener_growth_percent / 100.0)))::bigint
      else snapshot_previous_listeners
    end
  ),
  weekly_listener_gain = coalesce(
    weekly_listener_gain,
    case
      when weekly_listener_growth_percent is not null
        and snapshot_listeners is not null
        and weekly_listener_growth_percent > -99.9
      then snapshot_listeners - round(snapshot_listeners::numeric / (1 + (weekly_listener_growth_percent / 100.0)))::bigint
      else weekly_listener_gain
    end
  )
where
  weekly_listener_growth_percent is not null
  and snapshot_listeners is not null
  and (weekly_listener_gain is null or snapshot_previous_listeners is null);
