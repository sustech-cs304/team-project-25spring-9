import {
  mdiAccountCircle,
  mdiMonitor,
  mdiGithub,
  mdiLock,
  mdiAlertCircle,
  mdiSquareEditOutline,
  mdiImageMultiple,
  mdiTimelineClock
} from '@mdi/js'

export default [
  {
    to: '/dashboard',
    label: 'Dashboard',
    icon: mdiMonitor,
  },
  {
    to: '/photos',
    label: 'Photos',
    icon: mdiImageMultiple,
  },
  {
    to: '/albums',
    label: 'Albums',
    icon: mdiImageMultiple,
  },
  {
    to: '/timeline',
    label: 'Timeline',
    icon: mdiTimelineClock,
  },
  // {
  //   to: '/forms',
  //   label: 'Forms',
  //   icon: mdiSquareEditOutline,
  // },
  {
    to: '/profile',
    label: 'Profile',
    icon: mdiAccountCircle,
  },
  // {
  //   to: '/login',
  //   label: 'Login',
  //   icon: mdiLock,
  // },
  // {
  //   to: '/error',
  //   label: 'Error',
  //   icon: mdiAlertCircle,
  // },
  {
    href: 'https://github.com/sustech-cs304/team-project-25spring-9',
    label: 'GitHub',
    icon: mdiGithub,
    target: '_blank',
  }
]
