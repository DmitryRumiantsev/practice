SELECT name, COUNT(*) from users, messages WHERE messages.date = '2016-05-09' AND messages.user_id = users.id