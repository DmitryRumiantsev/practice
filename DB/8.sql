SELECT name, date, text FROM users, messages WHERE users.id = messages.user_id ORDER BY messages.date;