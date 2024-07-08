export default [
  {
    path: '/user', layout: false, routes: [
      {path: '/user/login', component: './User/Login'},
      {path: '/user/register', component: './User/Register'}
    ]
  },
  {name: '添加图表',path: '/add_chart', icon: 'ZoomIn', component: './AddChart'},
  {name: '我的图表',path: '/my_chart', icon: 'BarChart', component: './MyChart'},
  {path: '/welcome', icon: 'smile', component: './Welcome'},
  {
    path: '/admin',
    icon: 'crown',
    access: 'canAdmin',
    routes: [
      {path: '/admin', redirect: '/admin/sub-page'},
      {path: '/admin/sub-page', component: './Admin'},
    ],
  },
  {path: '/', redirect: '/welcome'},
  {path: '*', layout: false, component: './404'},
];
