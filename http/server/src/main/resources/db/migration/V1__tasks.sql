CREATE TABLE tasks (
    task_id uuid primary key,
    is_done boolean not null,
    text text not null
);