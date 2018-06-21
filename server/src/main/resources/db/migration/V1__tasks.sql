CREATE TABLE tasks (
    task_id uuid primary key,
    user_id uuid not null,
    is_done boolean not null,
    text text not null
);