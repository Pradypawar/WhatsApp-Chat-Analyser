import pandas
from urlextract import URLExtract
extract = URLExtract()

def fetch_stats(selected_user,df):

    if selected_user!='Overall':
        df =df[df['user']==selected_user]
    num_message = df.shape[0]
    words =[]
    for message in df['message']:
        words.extend(message.split())
    list_link =[]
    for link in df['message']:
        list_link.extend(extract.find_urls(link))

    # Fetch num of media shared
    num_media_shared = df[df['message']=='<Media omitted>'].shape[0]

    return num_message-num_media_shared,len(words)-(num_media_shared *2),num_media_shared,len(list_link)

def most_busy_users(df):
    x= df["user"].value_counts().head()
    return x