import streamlit as st
import pandas as pd
import plotly.express as px
import glob
import os

st.set_page_config(page_title="WSO Dashboard Wyników", layout="wide")

st.title("WSO 2026 Projekt - Wyniki eksperymentów")

# 1. Wczytywanie plików
files = glob.glob('DATA/wyniki_*.csv')
if not files:
    st.error("Brak plików w folderze DATA/. Uruchom eksperymenty!")
    st.stop()

# 2. Parsowanie plików do jednego DataFrame
data_frames = []
for file in files:
    # Wyciągamy nazwę: wyniki_{Algorytm}_{Wariant}_{Zadania}.csv
    filename = os.path.basename(file)
    parts = filename.replace('.csv', '').split('_')
    
    df = pd.read_csv(file)
    df['Algorytm'] = parts[1]
    df['Wariant'] = parts[2]
    df['LiczbaZadań'] = int(parts[3])
    data_frames.append(df)

df_all = pd.concat(data_frames, ignore_index=True)

# 3. Sidebar do filtrowania
st.sidebar.header("Filtry")
wariant_filter = st.sidebar.multiselect("Wariant:", df_all['Wariant'].unique(), default=df_all['Wariant'].unique())
alg_filter = st.sidebar.multiselect("Algorytm:", df_all['Algorytm'].unique(), default=df_all['Algorytm'].unique())

filtered_df = df_all[(df_all['Wariant'].isin(wariant_filter)) & (df_all['Algorytm'].isin(alg_filter))]

# 4. Wykres Makespanu
st.subheader("Średni czas wykonania (Makespan) wg algorytmu")

# --- TO JEST KLUCZOWE: najpierw definiujesz zmienną ---
avg_makespan = filtered_df.groupby(['Algorytm', 'LiczbaZadań', 'Wariant'])['Makespan'].mean().reset_index()

# --- A POTEM UŻYWASZ JEJ W WYKRESIE ---
fig = px.bar(
    avg_makespan, 
    x='LiczbaZadań', 
    y='Makespan', 
    color='Algorytm', 
    barmode='group',          
    facet_col='Wariant',      
    title="Makespan: Algorytmy vs Warianty",
    labels={'Makespan': 'Czas [s]'}
)

st.plotly_chart(fig, use_container_width=True)
# 5. Tabela z surowymi danymi
st.subheader("Surowe dane")
st.dataframe(filtered_df)