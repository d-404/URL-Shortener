--Tables (PostgreSQL):
create table short_url (
  id            bigint primary key,
  short_code    varchar(16) unique not null,
  target_url    text not null,
  created_at    timestamptz not null default now(),
  expires_at    timestamptz null,
  created_by    varchar(128) null,
  is_active     boolean not null default true
);

create table url_stats (
  short_code    varchar(16) primary key references short_url(short_code),
  total_clicks  bigint not null default 0,
  last_clicked  timestamptz
);

create index on short_url (short_code);
create index on short_url (expires_at);

select * from short_url;
select * from url_stats;