INSERT INTO users (name) VALUES ('test');

INSERT INTO boards DEFAULT VALUES;

INSERT INTO user_board VALUES (1, 1);

INSERT INTO statuses (name, board_id)
VALUES
    ('Todo', 1),
    ('Done', 1);

INSERT INTO tasks (title, description, startDate, endDate, board_id, status_id, assigner_id)
VALUES
    ('Task 1', 'Task 1 Description.', NOW() - INTERVAL '20' MINUTE, NOW() + INTERVAL '10' MINUTE, 1, 1, 1),
    ('Task 2', 'Task 2 Description.', NOW() - INTERVAL '10' MINUTE, NOW() + INTERVAL '20' MINUTE, 1, 2, 1);