import React from 'react';
import {
  LineChart, Line, XAxis, YAxis, Tooltip, Legend, CartesianGrid, ResponsiveContainer
} from 'recharts';

function transformData(data) {
  const grouped = {};

  data.forEach(entry => {
    const time = new Date(entry.timestamp).toISOString().slice(0, 16).replace('T', ' ');
    if (!grouped[time]) grouped[time] = { timestamp: time };
    grouped[time][entry.zone] = entry.load ?? 0;
  });

  return Object.values(grouped).sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp));
}

function getUniqueZones(data) {
  const zones = new Set(data.map(item => item.zone));
  return Array.from(zones);
}

function LoadChart({ data }) {
  if (!data || data.length === 0) return null;

  const chartData = transformData(data);
  const zones = getUniqueZones(data);
  const colors = [
    '#8884d8', '#82ca9d', '#ffc658', '#ff7300', '#00c49f',
    '#0088fe', '#a83279', '#aa46be', '#ff6e6e', '#a0d911'
  ];

  return (
    <ResponsiveContainer width="100%" height={500}>
      <LineChart data={chartData} margin={{ top: 20, right: 30, left: 20, bottom: 20 }}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="timestamp" tick={{ fontSize: 10 }} />
        <YAxis />
        <Tooltip formatter={(value) => value.toFixed(2)} />
        <Legend />
        {zones.map((zone, index) => (
          <Line
            key={zone}
            type="monotone"
            dataKey={zone}
            stroke={colors[index % colors.length]}
            dot={false}
            name={zone}
          />
        ))}
      </LineChart>
    </ResponsiveContainer>
  );
}

export default LoadChart;
