insert into user_accounts (email, full_name, password_hash, bio, role, admin, support_mode)
values
    ('admin@demo.local', 'Ada Admin', '482c811da5d5b4bc6d497ffa98491e38', 'Runs the demo instance.', 'ADMIN', true, false),
    ('alex@acme.local', 'Alex Analyst', '482c811da5d5b4bc6d497ffa98491e38', 'Owns the acme workspace.', 'USER', false, false),
    ('beth@beta.local', 'Beth Builder', '482c811da5d5b4bc6d497ffa98491e38', 'Works in beta workspace.', 'MANAGER', false, false),
    ('sam@support.local', 'Sam Support', '482c811da5d5b4bc6d497ffa98491e38', 'Support rep with fake tooling access.', 'SUPPORT', false, true);

insert into workspaces (name, slug, visibility, owner_id)
values
    ('Acme Workspace', 'acme', 'PRIVATE', (select id from user_accounts where email = 'alex@acme.local')),
    ('Beta Workspace', 'beta', 'PRIVATE', (select id from user_accounts where email = 'beth@beta.local')),
    ('Public Demo Space', 'public-demo', 'PUBLIC', (select id from user_accounts where email = 'admin@demo.local'));

insert into memberships (user_id, workspace_id, role)
values
    ((select id from user_accounts where email = 'admin@demo.local'), (select id from workspaces where slug = 'acme'), 'OWNER'),
    ((select id from user_accounts where email = 'admin@demo.local'), (select id from workspaces where slug = 'beta'), 'OWNER'),
    ((select id from user_accounts where email = 'admin@demo.local'), (select id from workspaces where slug = 'public-demo'), 'OWNER'),
    ((select id from user_accounts where email = 'alex@acme.local'), (select id from workspaces where slug = 'acme'), 'OWNER'),
    ((select id from user_accounts where email = 'beth@beta.local'), (select id from workspaces where slug = 'beta'), 'OWNER'),
    ((select id from user_accounts where email = 'sam@support.local'), (select id from workspaces where slug = 'acme'), 'SUPPORT');

insert into projects (workspace_id, owner_id, name, description, status, private_project, published, webhook_url)
values
    ((select id from workspaces where slug = 'acme'), (select id from user_accounts where email = 'alex@acme.local'), 'Quarterly Planning', 'Roadmap and budget alignment notes.', 'ACTIVE', true, false, 'http://localhost:9999/hook/acme'),
    ((select id from workspaces where slug = 'beta'), (select id from user_accounts where email = 'beth@beta.local'), 'Beta Launch', 'Early launch checklist with partner feedback.', 'DRAFT', true, false, 'http://localhost:9999/hook/beta'),
    ((select id from workspaces where slug = 'public-demo'), (select id from user_accounts where email = 'admin@demo.local'), 'Shared Showcase', 'A public-facing list of features for demo visitors.', 'PUBLISHED', false, true, 'http://localhost:9999/hook/public');

insert into project_comments (project_id, author_id, body, internal_note)
values
    ((select id from projects where name = 'Quarterly Planning'), (select id from user_accounts where email = 'alex@acme.local'), 'Need to confirm pricing assumptions before next review.', false),
    ((select id from projects where name = 'Quarterly Planning'), (select id from user_accounts where email = 'sam@support.local'), '<strong>Support note:</strong> customer asked for export replay.', true),
    ((select id from projects where name = 'Beta Launch'), (select id from user_accounts where email = 'beth@beta.local'), 'Draft scope is still changing this week.', false),
    ((select id from projects where name = 'Shared Showcase'), (select id from user_accounts where email = 'admin@demo.local'), 'Public showcase is safe for guests.', false);

insert into invite_tokens (workspace_id, email_hint, token, invited_role, claimed_by_user_id, expires_at)
values
    ((select id from workspaces where slug = 'acme'), 'newuser@acme.local', 'invite-acme-demo-token', 'MEMBER', null, '2099-12-31 00:00:00'),
    ((select id from workspaces where slug = 'beta'), 'ops@beta.local', 'invite-beta-demo-token', 'MANAGER', null, '2099-12-31 00:00:00');
