export default [
  {
    path: '/user', layout: false, routes: [
      {path: '/user/login', component: './User/Login'},
      {path: '/user/register', component: './User/Register'}
    ]
  },
  {path: '/', redirect: '/add_chart'},
  {name: '添加图表',path: '/add_chart', icon: 'smile', component: './AddChart'},
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
