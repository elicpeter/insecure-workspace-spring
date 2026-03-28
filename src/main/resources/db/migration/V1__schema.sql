create table user_accounts (
    id bigserial primary key,
    email varchar(255) not null unique,
    full_name varchar(255) not null,
    password_hash varchar(255) not null,
    bio text,
    role varchar(50) not null,
    admin boolean not null default false,
    support_mode boolean not null default false,
    created_at timestamp not null default current_timestamp
);

create table workspaces (
    id bigserial primary key,
    name varchar(255) not null,
    slug varchar(255) not null unique,
    visibility varchar(50) not null,
    owner_id bigint references user_accounts(id),
    created_at timestamp not null default current_timestamp
);

create table memberships (
    id bigserial primary key,
    user_id bigint not null references user_accounts(id),
    workspace_id bigint not null references workspaces(id),
    role varchar(50) not null,
    unique(user_id, workspace_id)
);

create table projects (
    id bigserial primary key,
    workspace_id bigint not null references workspaces(id),
    owner_id bigint not null references user_accounts(id),
    name varchar(255) not null,
    description text,
    status varchar(50) not null,
    private_project boolean not null default true,
    published boolean not null default false,
    webhook_url varchar(500),
    created_at timestamp not null default current_timestamp
);

create table project_comments (
    id bigserial primary key,
    project_id bigint not null references projects(id),
    author_id bigint not null references user_accounts(id),
    body text not null,
    internal_note boolean not null default false,
    created_at timestamp not null default current_timestamp
);

create table attachments (
    id bigserial primary key,
    project_id bigint not null references projects(id),
    uploader_id bigint not null references user_accounts(id),
    original_filename varchar(255) not null,
    storage_path varchar(500) not null,
    content_type varchar(255),
    created_at timestamp not null default current_timestamp
);

create table invite_tokens (
    id bigserial primary key,
    workspace_id bigint not null references workspaces(id),
    email_hint varchar(255),
    token varchar(255) not null unique,
    invited_role varchar(50) not null,
    claimed_by_user_id bigint references user_accounts(id),
    expires_at timestamp,
    created_at timestamp not null default current_timestamp
);

create table audit_events (
    id bigserial primary key,
    actor_id bigint references user_accounts(id),
    workspace_id bigint references workspaces(id),
    event_type varchar(100) not null,
    details text,
    created_at timestamp not null default current_timestamp
);
