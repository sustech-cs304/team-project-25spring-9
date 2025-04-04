# OOAD-BACKEND-PROJECT

# 在更新服务器版本前请务必在本地进行测试！！！

权限管理文档：https://sa-token.cc/doc.html

多表联查文档：https://mybatis-plus-join.github.io

注意！！！浏览器在使用signin登录后会记录cookie，维持登录状态，在使用signout登出后才会忘记登陆状态

`user`:

- `/user`

    - `/signin`：登录

        - 查询参数：

        - `userPassword`

        - `userName`

        - 返回：

        - {

              "code": 501,

              "msg": "用户名不存在",

              "data": **null**,

              "dataCount": **null**

            }

        - {

              "code": 502,

              "msg": "用户名或密码错误",

              "data": **null**,

              "dataCount": **null**

            }

        - {

              "code": 200,

              "msg": "success!",

              "data": **null**,

              "dataCount": **null**

            }

    - `/list`：显示全部用户信息（包括账户，密码）需要权限：admin
        
        - 返回：
        - `userId`
        - `userName`
        - `userPassword`
    - `/new`：新增用户
        - 查询参数：
        - `userPassword`
        - `userName`
        - 返回：T/F
    - `/islogin`: 检查是否登录

        - 查询参数：null
        - 返回：
        - {

              "code": 401,

              "msg": "token 无效：549530c3-dd5d-4a46-8292-1d0585359f1f",

              "data": **null**,

              "dataCount": **null**

            }

        - {

              "code": 200,

              "msg": "success！",

              "data": [

                ​    "student",
    
                ​    "admin",

                ​    "manager"

              ],

              "dataCount": **null**

            }
    - `/username`：查询用户名是否存在

        - 查询参数：
        - `userName`
        - 返回：
        - {

                "code": 200,

                "msg": "success!",

                "data": **null**,

                "dataCount": **null**

          }
        - {

                "code": 501,

                "msg": "用户名不存在",

                "data": **null**,

                "dataCount": **null**

          }
- record系列: 给出所有可关联的数据，用于输入输出检测
- search系列: 根据前端需要给出数据，用于模型正常实现
- `/building`
    - `/record`: 添加建筑
        - 查询参数：（emm，不清楚，问高哥doge）
        - {

              成功："success adding a building"
     
              否则："Fail to add a building"
            
          }
    - `/recordAll`: 列出所有建筑及全部相关信息
        - 查询参数：
        - 返回：
        - {

              建筑：id（buildingId），名字（buildingName），图片（buildingImg），描述（buildingDescription），横、纵坐标（buildingX、buildingY），相关话题（comments（一个list））
            
              话题：id（commentId），评论用户id（userId），所属建筑信息（buildingId），话题内容（commentContent），上传时间（commentTime），是否可见（commentValid（用于管理员权限实现）），点赞成员（commentLiked（由用户id组成的list）），发帖用户详细信息（userInformation（userId, userName, userPassword, userImg）），相关回复（replies（一个list））
            
              评论：id（replyId），进行评论的用户id（userId），被评论的用户id（targetId），所属话题id（commentId），评论内容（replyContent），上传时间（replyTime），是否可见（replyValid（用于管理员权限实现）），点赞成员（replyLiked（由用户id组成的list）），user详细信息（userInformation（userId, userName, userPassword, userImg）），target详细信息（targetId, targetName, targetPassword, targetImg））
          
            }
    - `/recordById`
        - 查询参数:
        - `Id`
        - 返回：指定建筑信息，内容同上
    - `/searchAll`
        - 查询参数:
        - 返回:
        - {

              建筑id: buildingId

              建筑名称: buildingName
          
            }
    - `/searchById`
        - 查询参数:
        - 返回:
        - {

              建筑id: buildingId

              建筑名称: buildingName

              建筑图片: buildingImg

              建筑描述: buildingDescription
     
              可见的相关话题：comments（存有话题id的list）
              
            } 
          
