import preprocessor
import helper
import pandas as pd


def upload_file(data):
    global  df
    df = preprocessor.preprocess(data)
    return  df

def user_list():
    global user_list
    user_list = df['user'].unique().tolist()
    user_list.remove('group_notification')
    user_list.sort()
    user_list.insert(0,'Overall')
    return  user_list


def selected_user(selected_users):
    num_messages, words,media_shared,num_link= helper.fetch_stats(selected_users,df)

    return  num_messages,words,media_shared,num_link
def graph_points():
    return helper.most_busy_users(df)