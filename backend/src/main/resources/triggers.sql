# �����۵���
create definer = root@`%` trigger if not exists add_likes_to_comment
    after insert
    on comment_likes
    for each row
begin
    update comment set comment_likes = comment_likes + 1 where comment_id = new.comment_id;
end;
# ȡ�������۵ĵ���
create definer = root@`%` trigger if not exists remove_likes_to_comment
    after delete
    on comment_likes
    for each row
begin
    update comment set comment_likes = comment_likes - 1 where comment_id = old.comment_id;
end;
# ���ظ�����
create definer = root@`%` trigger if not exists add_likes_to_reply
    after insert
    on reply_likes
    for each row
begin
    update reply set reply_likes = reply_likes + 1 where reply_id = new.reply_id;
end;
# ȡ���Իظ��ĵ���
create definer = root@`%` trigger if not exists remove_likes_to_reply
    after delete
    on reply_likes
    for each row
begin
    update reply set reply_likes = reply_likes - 1 where reply_id = old.reply_id;
end;
# ����Ա���һ������
create definer = root@`%` trigger if not exists add_comments_to_building_admin
    after insert
    on comment
    for each row
begin
    update building
    set building_comments = building.building_comments + 1
    where building_id = new.building_id
      and new.comment_valid = 1;
end;
# ͨ��һ������
create definer = root@`%` trigger if not exists add_comments_to_building
    after update
    on comment
    for each row
begin
    update building
    set building_comments = building.building_comments + 1
    where building_id = new.building_id
      and new.comment_valid = 1
      and old.comment_valid != 1;
end;
# �¼�һ������
create definer = root@`%` trigger if not exists minus_comments_to_building
    after update
    on comment
    for each row
begin
    update building
    set building_comments = building.building_comments - 1
    where building_id = new.building_id
      and new.comment_valid != 1
      and old.comment_valid = 1;
end;
# ɾ��һ������
create definer = root@`%` trigger if not exists remove_comments_to_building
    after delete
    on comment
    for each row
begin
    update building
    set building_comments = building.building_comments - 1
    where building_id = old.building_id
      and old.comment_valid = 1;
end;
# ����Ա���һ���ظ�
create definer = root@`%` trigger if not exists add_replies_to_comment_admin
    after insert
    on reply
    for each row
begin
    update comment
    set comment_replies = comment.comment_replies + 1
    where comment_id = new.comment_id
      and new.reply_valid = 1;
end;
# ͨ��һ���ظ�
create definer = root@`%` trigger if not exists add_replies_to_comment
    after update
    on reply
    for each row
begin
    update comment
    set comment_replies = comment.comment_replies + 1
    where comment_id = new.comment_id
      and new.reply_valid = 1
      and old.reply_valid != 1;
end;
# �¼�һ���ظ�
create definer = root@`%` trigger if not exists minus_replies_to_comment
    after update
    on reply
    for each row
begin
    update comment
    set comment_replies = comment.comment_replies - 1
    where comment_id = new.comment_id
      and new.reply_valid != 1
      and old.reply_valid = 1;
end;
# ɾ��һ���ظ�
create definer = root@`%` trigger if not exists remove_replies_to_comment
    after delete
    on reply
    for each row
begin
    update comment
    set comment_replies = comment.comment_replies - 1
    where comment_id = old.comment_id
      and old.reply_valid = 1;
end;
# ��ӽ�����Ƭ
create definer = root@`%` trigger if not exists add_img_to_building
    after insert
    on building_img
    for each row
begin
    update building
    set building_img = building.building_img + 1
    where building_id = new.building_id;
end;
# ɾ��������Ƭ
create definer = root@`%` trigger if not exists remove_img_to_building
    after delete
    on building_img
    for each row
begin
    update building
    set building_img = building.building_img - 1
    where building_id = old.building_id;
end;
# ���������Ƭ
create definer = root@`%` trigger if not exists add_img_to_comment
    after insert
    on comment_img
    for each row
begin
    update comment
    set comment_img = comment.comment_img + 1
    where comment_id = new.comment_id;
end;
# ɾ��������Ƭ
create definer = root@`%` trigger if not exists remove_img_to_comment
    after delete
    on comment_img
    for each row
begin
    update comment
    set comment_img = comment.comment_img - 1
    where comment_id = old.comment_id;
end;

# �߸粿��
create definer = root@`%` trigger if not exists update_user_after_insert
    before insert
    on user
    for each row
BEGIN
    IF NEW.user_img IS NULL THEN
        SET NEW.user_img = CONCAT(NEW.user_id, '.jpeg');
    END IF;

    -- ����user_mail
    IF NEW.user_mail IS NULL THEN
        SET NEW.user_mail = CONCAT(NEW.user_name, '@mail.sustech.edu.cn');
    END IF;

    -- ����user_nickname
    IF NEW.user_nickname IS NULL THEN
        SET NEW.user_nickname = NEW.user_name;
    END IF;
END;
create definer = root@`%` trigger if not exists pay
    before update
    on orders
    for each row
BEGIN
    IF NEW.pay_time is not null and OLD.pay_time is null THEN
        SET NEW.payed = true;
    END IF;
END;
create definer = root@`%` trigger if not exists order_delete_food
    before delete
    on food_order
    for each row
BEGIN
    update food
    set food.food_ordered_amount=food_ordered_amount + OLD.food_amount
    where food.food_id = OLD.food_id;
    IF (SELECT order_taken from orders where orders.order_id = OLD.order_id) = FALSE THEN
        IF (SELECT COUNT(*) FROM food_rank_date WHERE date = CURDATE() and food_rank_date.food_id = OLD.food_id) =
           0 THEN
            insert into food_rank_date (food_id, food_cnt, date) VALUES (OLD.food_id, 0, CURDATE());
        END IF;
        UPDATE food_rank_date
        SET food_cnt = food_cnt - OLD.food_amount
        WHERE date = CURDATE()
          and food_rank_date.food_id = OLD.food_id;
    END IF;
END;
create definer = root@`%` trigger if not exists order_new_food
    before insert
    on food_order
    for each row
BEGIN
    update food
    set food.food_ordered_amount=food_ordered_amount + NEW.food_amount
    where food.food_id = NEW.food_id;
    IF (SELECT COUNT(*) FROM food_rank_date WHERE date = CURDATE() and food_rank_date.food_id = NEW.food_id) = 0 THEN
        insert into food_rank_date (food_id, food_cnt, date) VALUES (NEW.food_id, 0, CURDATE());
    END IF;
    UPDATE food_rank_date
    SET food_cnt = food_cnt + NEW.food_amount
    WHERE date = CURDATE()
      and food_rank_date.food_id = NEW.food_id;
END;

show triggers;
