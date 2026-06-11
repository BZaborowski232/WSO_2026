import streamlit as st
import pandas as pd
import plotly.express as px
import glob
import os

st.set_page_config(page_title="WSO Dashboard Wyników", layout="wide")
st.title("WSO 2026 Projekt - Wyniki eksperymentów")

# 1. Wczytywanie plików MAKESPAN
files = glob.glob('DATA/wyniki_*.csv')
if not files:
    st.error("Brak plików w folderze DATA/. Uruchom eksperymenty!")
    st.stop()

data_frames = []
for file in files:
    filename = os.path.basename(file)
    parts = filename.replace('.csv', '').split('_')
    df = pd.read_csv(file)
    df['Algorytm'] = parts[1]
    df['Wariant'] = parts[2]
    df['LiczbaZadań'] = str(parts[3]) # Zmienione na str, żeby ładnie wyglądało na osi X
    data_frames.append(df)

df_all = pd.concat(data_frames, ignore_index=True)

# 2. Wczytywanie plików ENERGIA
energy_files = glob.glob('DATA/energia_*.csv')
df_energy = pd.DataFrame()
if energy_files:
    energy_frames = [pd.read_csv(f) for f in energy_files]
    df_energy = pd.concat(energy_frames, ignore_index=True)
    df_energy['LiczbaZadań'] = df_energy['LiczbaZadań'].astype(str)

# 3. Sidebar do filtrowania
st.sidebar.header("Filtry")
wariant_filter = st.sidebar.multiselect("Wariant:", df_all['Wariant'].unique(), default=df_all['Wariant'].unique())
alg_filter = st.sidebar.multiselect("Algorytm:", df_all['Algorytm'].unique(), default=df_all['Algorytm'].unique())

filtered_df = df_all[(df_all['Wariant'].isin(wariant_filter)) & (df_all['Algorytm'].isin(alg_filter))]

# 4. Wykres Makespanu
st.subheader("Średni czas wykonania (Makespan) wg algorytmu")
avg_makespan = filtered_df.groupby(['Algorytm', 'LiczbaZadań', 'Wariant'])['Makespan'].mean().reset_index()

fig1 = px.bar(
    avg_makespan, 
    x='LiczbaZadań', 
    y='Makespan', 
    color='Algorytm', 
    barmode='group',          
    facet_col='Wariant',      
    title="Makespan: Algorytmy vs Warianty",
    labels={'Makespan': 'Czas [s]'}
)
st.plotly_chart(fig1, use_container_width=True)

# 5. Wykres Energii (Jeśli są pliki)
if not df_energy.empty:
    st.subheader("Efektywność energetyczna (Zużycie energii)")
    filtered_energy = df_energy[(df_energy['Wariant'].isin(wariant_filter)) & (df_energy['Algorytm'].isin(alg_filter))]
    
    fig2 = px.bar(
        filtered_energy, 
        x='LiczbaZadań', 
        y='Energia_kWh', 
        color='Algorytm', 
        barmode='group',          
        facet_col='Wariant',      
        title="Całkowite zużycie energii centrum danych",
        labels={'Energia_kWh': 'Energia [kWh]'},
        color_discrete_sequence=px.colors.qualitative.Pastel # Inny zestaw kolorów dla energii
    )
    st.plotly_chart(fig2, use_container_width=True)

# 6. Tabela z surowymi danymi
st.subheader("Surowe dane")
st.dataframe(filtered_df)