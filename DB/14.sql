SELECT name, date, text FROM users, messages WHERE messages.user_id = users.id AND messages.text LIKE '%keyword%'