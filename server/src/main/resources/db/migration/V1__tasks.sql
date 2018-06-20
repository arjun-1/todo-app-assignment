CREATE TABLE tasks (
    task_id uuid primary key,
    user_id uuid not null,
    is_done boolean not null,
    text text not null
);

--INSERT INTO tasks (task_id, user_id, is_done, text) VALUES ('bbec2d34-e097-4294-9c35-6653caf1024a', 'bbec2d34-e097-4294-9c35-6653caf1024a', true, 'hoihoi');
