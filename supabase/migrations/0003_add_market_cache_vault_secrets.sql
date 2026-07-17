do $$
declare
  project_url_secret_id uuid;
  market_secret_id uuid;
begin
  select id
  into project_url_secret_id
  from vault.secrets
  where name = 'project_url';

  if project_url_secret_id is null then
    perform vault.create_secret(
      'https://kwscgmxfguxknmnhpxfd.supabase.co',
      'project_url'
    );
  else
    perform vault.update_secret(
      project_url_secret_id,
      'https://kwscgmxfguxknmnhpxfd.supabase.co'
    );
  end if;

  select id
  into market_secret_id
  from vault.secrets
  where name = 'market_refresh_secret';

  if market_secret_id is null then
    perform vault.create_secret(
      'jLACSXmJWtTCQoHd4pND9AN59eGU3khV3F32fQnwYtdKbtPwwkdmpvbqnBbBHHbZ',
      'market_refresh_secret'
    );
  else
    perform vault.update_secret(
      market_secret_id,
      'jLACSXmJWtTCQoHd4pND9AN59eGU3khV3F32fQnwYtdKbtPwwkdmpvbqnBbBHHbZ'
    );
  end if;
end
$$;
